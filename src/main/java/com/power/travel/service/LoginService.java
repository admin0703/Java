package com.power.travel.service;

import com.power.travel.core.Result;
import com.power.travel.core.ResultGenerator;
import com.power.travel.model.ToEmail;
import com.power.travel.model.User;
import com.power.travel.util.CookieUitl;
import com.power.travel.util.IdGenerator;
import com.power.travel.repository.UserRepository;
import com.power.travel.util.VerCodeGenerateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Service
public class LoginService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    RedisTemplate redisTemplate;

    //	获得发件人信息
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private UserRepository userRepository;

    public Result login(User user, HttpServletResponse response) {
        User userByUsername = userRepository.findUserByUsername(user.getUsername());
        if (userByUsername == null) {
            return ResultGenerator.genFailResult("用户名错误!");
        } else {
            if (user.getPassword().equals(userByUsername.getPassword())) {

                Cookie cookie = new Cookie("username", user.getUsername());
                cookie.setPath("/");
                cookie.setMaxAge(3600);
                response.addCookie(cookie);
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult("密码错误!");
            }
        }

    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = CookieUitl.get(request, "username");
        if(cookie != null){
            CookieUitl.set(response,"username",null,0);
        }


//        String value = null;
//
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null){
//            System.out.println("error");
//        }else{
//            for (int i = 0;i<cookies.length;i++){
//                if(cookies[i].getName().equals("root")){
//                    value = cookies[i].getValue();
//                }
//            }
//        }
//
//        Cookie cookie = new Cookie("username",value);
//        cookie.setMaxAge(-1);
    }

    public Result register(User user) {
        User userByUsername = userRepository.findUserByUsername(user.getUsername());
        if(userByUsername != null){
            return ResultGenerator.genFailResult("用户名重复!");
        }
        //Todo 这里有一个事务操作
        user.setId(IdGenerator.id());
        userRepository.save(user);
        return ResultGenerator.genSuccessResult();
    }

    @Async
    public Result sendEmail(Map<String,String> toEmail, HttpServletRequest request){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        message.setTo(toEmail.get("email"));

        message.setSubject("您本次的验证码是");

        String verCode = VerCodeGenerateUtil.generateVerCode();
        User rece=userRepository.findUserByEmail(toEmail.get("email"));
        message.setText("尊敬的"+rece.getUsername()+",您好:\n"
                + "\n本次请求的邮件验证码为:" + verCode + ",本验证码 5 分钟内效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");
        mailSender.send(message);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        /*设置缓存*/
        valueOperations.set(toEmail.get("email"),verCode);
        /**
         * K key, final long timeout, final TimeUnit unit
         * key 存储数据的key值
         * TimeUnit 时间单位
         * timeout 数据的过期时间
         * */
        redisTemplate.expire(verCode,60*5, TimeUnit.SECONDS);
        return ResultGenerator.genSuccessResult("发送成功");
    }

    public Result checkCode(String code,String email){
        if (verification(code,email)){
            return ResultGenerator.genSuccessMsgResult(email);
        }else {
            return ResultGenerator.genFailResult("验证码错误");
        }
    }
//    校验验证码
    public Boolean verification(String code,String email){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        /*从redis中获取验证码*/
        String verificationCode = (String) valueOperations.get(email);
        /*判断提交的信息是否正确*/
        if(verificationCode!=null&&verificationCode.equals(code)){
            /*删除缓存中的数据*/
            redisTemplate.delete(code);
            return true;
        }
        return false;
    }
//    邮箱验证修改密码
    public Result resetPw(String pw,String email){
        User user=userRepository.findUserByEmail(email);
        user.setPassword(pw);
        userRepository.save(user);
        return ResultGenerator.genSuccessResult();
    }
}
