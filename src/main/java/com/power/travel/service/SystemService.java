package com.power.travel.service;

import com.power.travel.core.Result;
import com.power.travel.core.ResultGenerator;
import com.power.travel.core.ServiceException;
import com.power.travel.enums.StatusEnum;
import com.power.travel.model.*;
import com.power.travel.repository.*;
import com.power.travel.util.CookieUitl;
import com.power.travel.util.ExportExcel;
import com.power.travel.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class SystemService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserHotelRepository userHotelRepository;
    @Autowired
    private UserAttractionsRepository userAttractionsRepository;
    @Autowired
    private UserRouteRepository userRouteRepository;
    @Autowired
    private UserStrategyRepository userStrategyRepository;
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AttractionsRepository attractionsRepository;

    @Autowired
    private TravelRouteRepository travelRouteRepository;
    @Autowired
    private ReserveService reserveService;
    @Autowired
    private TravelStrategyRepository travelStrategyRepository;

    private Random random = new Random(100);

    public Result login(SysUser sysUser, HttpServletResponse response) {


        SysUser sysUserByUsrname = sysUserRepository.findSysUserByUsername(sysUser.getUsername());
        if (sysUserByUsrname == null) {
            return ResultGenerator.genFailResult("用户名错误！");
        } else {
            if (sysUser.getPassword().equals(sysUserByUsrname.getPassword())) {
                CookieUitl.set(response, "sysUsername", sysUser.getUsername(), 3600);
                return ResultGenerator.genSuccessResult();
            } else {
                return ResultGenerator.genFailResult("密码错误");
            }
        }

    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = CookieUitl.get(request, "sysUsername");
        if (cookie != null) {
            CookieUitl.set(response, "sysUsername", null, 0);
        }
    }

    public Page<User> getUserPage(Pageable pageable) {
        Page<User> userPage = userRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.where(predicates.toArray(new Predicate[]{}));
            query.orderBy(cb.desc(root.get("id")));
            return null;
        }, pageable);
        return userPage;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result saveUser(User user) {

        if (StringUtils.isEmpty(user.getId())) {//没有id的情况
            user.setId(IdGenerator.id());
        } else {
            User oldUser = getUserById(user.getId());
            user.setUsername(oldUser.getUsername());
            user.setName(oldUser.getName());
            oldUser.setPassword(user.getPassword());
        }

        userRepository.saveAndFlush(user);
        return ResultGenerator.genSuccessResult();
    }
    public Result sysSave(User user){
        User old=getUserById(user.getId());
        old.setUsername(user.getUsername());
        old.setEmail(user.getEmail());
        old.setGender(user.getGender());
        old.setPhone(user.getPhone());
        userRepository.save(old);
        return ResultGenerator.genSuccessResult();
    }
    @Transactional(rollbackFor = Exception.class)
    public Result sysDel(String id){
            userAttractionsRepository.deleteByUser_Id(id);
            userHotelRepository.deleteUserHotelByUser_Id(id);
            userStrategyRepository.deleteUserStrategyByUserId(id);
            userRouteRepository.deleteByUser_Id(id);
            userRepository.deleteById(id);
            return ResultGenerator.genSuccessResult();
    }
    public User getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ServiceException("用户ID错误"));
        return user;
    }

    //    折线图数据
    public HashMap<String,Integer> getChartData(){
        List<UserHotel> list=userHotelRepository.findAll();
        //统计的是不同日期的当天预定人数
        HashMap<String, Integer> sites = new HashMap<String, Integer>();
        for (int i=0;i<list.size();i++){
                String str=list.get(i).getCreateDate().toString().split(" ")[0];
                int value;
//                首次创建
                if (sites.get(str)==null){
                    value=1;
                }
                else {
                    value=sites.get(str).intValue()+1;
                }
                sites.put(str,value);
        }
        return sites;
    }
    //    饼图数据
    public HashMap<String,Integer>  getChartPart2(){
        List<Hotel> list=hotelRepository.findAll();
        List<UserHotel> list2=userHotelRepository.findAll();
        HashMap<String, Integer> sites = new HashMap<String, Integer>();
        List<String> names=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            names.add(list.get(i).getName());
            sites.put(list.get(i).getName(),0);
        }
        for (int i = 0; i <list2.size() ; i++) {
            for (int j = 0; j <list.size() ; j++) {
                if (list2.get(i).getHotel().getId().equals(list.get(j).getId())){
                        sites.put(list.get(j).getName(),sites.get(list.get(j).getName())+1);
                        break;
                    }
                }
            }
        return sites;
    }


    public Page<Hotel> getHotelPage(Pageable pageable) {
        Page<Hotel> hotelPage = hotelRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.where(predicates.toArray(new Predicate[]{}));
            query.orderBy(cb.desc(root.get("createDate")));
            return null;
        }, pageable);
        return hotelPage;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result saveHotel(Hotel hotel) {

        if (StringUtils.isEmpty(hotel.getId())) {//没有id的情况
            hotel.setId(IdGenerator.id());
            hotel.setStatus(StatusEnum.DOWM_STATUS.getCode());
            hotel.setCreateDate(new Date());
            int i = random.nextInt(100);
            hotel.setImage("MY_kezhan_0" + (i % 8 + 1));
        } else {
            //有id的情况
            Hotel oldHotel = getHotelById(hotel.getId());
            hotel.setStatus(oldHotel.getStatus());
            hotel.setCreateDate(oldHotel.getCreateDate());
            hotel.setImage(oldHotel.getImage());
        }
        hotelRepository.saveAndFlush(hotel);
        return ResultGenerator.genSuccessMsgResult(hotel.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public Result saveImg(MultipartFile fileUpload,String id){
            Hotel oldHotel = getHotelById(id);
            //获取文件名
            String fileName = fileUpload.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成文件名
            fileName = IdGenerator.id()+suffixName;
            //指定本地文件夹存储图片，写到需要保存的目录下
            String filePath = "D:\\javaText\\";
            if (!fileUpload.isEmpty()){
                try {
                    //将图片保存到static文件夹里

                    fileUpload.transferTo(new File(filePath+fileName));
                    oldHotel.setImage("http://localhost:8080/travel/image/"+fileName);
                    //返回提示信息
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ResultGenerator.genSuccessResult();
            }
            else
            {
                return ResultGenerator.genFailResult("null object");
            }

        }
    @Transactional(rollbackFor = Exception.class)
    public Result saveAttrImg(MultipartFile fileUpload,String id){
       Attractions oldHotel = getAttractionsById(id);
        //获取文件名
        String fileName = fileUpload.getOriginalFilename();
        //获取文件后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //重新生成文件名
        fileName = IdGenerator.id()+suffixName;
        //指定本地文件夹存储图片，写到需要保存的目录下
        String filePath = "D:\\javaText\\";
        if (!fileUpload.isEmpty()){
            try {
                //将图片保存到static文件夹里

                fileUpload.transferTo(new File(filePath+fileName));
                oldHotel.setImage("http://localhost:8080/travel/image/"+fileName);
                //返回提示信息
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResultGenerator.genSuccessResult();
        }
        else
        {
            return ResultGenerator.genFailResult("null object");
        }

    }
    public Hotel getHotelById(String id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ServiceException("酒店ID错误!"));
        return hotel;
    }

    public Result updateStatus(String id) {
        Hotel hotel = getHotelById(id);
        if (hotel.getStatus().equals(StatusEnum.DOWM_STATUS.getCode())) {
            //改变状态
            hotel.setStatus(StatusEnum.UP_STATUS.getCode());
        } else {
            hotel.setStatus(StatusEnum.DOWM_STATUS.getCode());
        }
        hotelRepository.saveAndFlush(hotel);
        return ResultGenerator.genSuccessResult();
    }

    public Page<Attractions> getAttractionsPage(Pageable pageable) {
        Page<Attractions> attractionsPage = attractionsRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.where(predicates.toArray(new Predicate[]{}));
            query.orderBy(cb.desc(root.get("createDate")));
            return null;
        }, pageable);
        return attractionsPage;
    }

    public Attractions getAttractionsById(String id) {
        Attractions attractions = attractionsRepository.findById(id).orElseThrow(() -> new ServiceException("景点ID错误"));
        return attractions;
    }

    public Result updateAttractionsStatus(String id) {
        Attractions attractions = getAttractionsById(id);
        if (attractions.getStatus().equals(StatusEnum.DOWM_STATUS.getCode())) {
            //改变状态
            attractions.setStatus(StatusEnum.UP_STATUS.getCode());
        } else {
            attractions.setStatus(StatusEnum.DOWM_STATUS.getCode());
        }
        attractionsRepository.saveAndFlush(attractions);
        return ResultGenerator.genSuccessResult();
    }

    @Transactional(rollbackFor = Exception.class)
    public Result saveAttractions(Attractions attractions) {
        if (StringUtils.isEmpty(attractions.getId())) {//没有id的情况
            attractions.setId(IdGenerator.id());
            attractions.setStatus(StatusEnum.DOWM_STATUS.getCode());
            attractions.setCreateDate(new Date());
            int i = random.nextInt(100);
            attractions.setImage("MY_jingdian_0" + (i % 8 + 1));
        } else {
            //有id的情况
            Attractions oldAttractions = getAttractionsById(attractions.getId());
            attractions.setStatus(oldAttractions.getStatus());
            attractions.setCreateDate(oldAttractions.getCreateDate());
            attractions.setImage(oldAttractions.getImage());
        }
        attractionsRepository.saveAndFlush(attractions);
        return ResultGenerator.genSuccessMsgResult(attractions.getId());
    }

    public Page<TravelRoute> getTravelRoutePage(Pageable pageable) {
        Page<TravelRoute> travelRoutePage = travelRouteRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.where(predicates.toArray(new Predicate[]{}));
            query.orderBy(cb.desc(root.get("createDate")));
            return null;
        }, pageable);
        return travelRoutePage;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result saveTravelRoute(TravelRoute travelRoute) {

        if (StringUtils.isEmpty(travelRoute.getId())) {//没有id的情况
            travelRoute.setId(IdGenerator.id());
            if (travelRoute.getStatus() == null) {
                //默认为停用
                travelRoute.setStatus(StatusEnum.DOWM_STATUS.getCode());
                travelRoute.setCollectNumber(0);
                travelRoute.setCreateDate(new Date());
            }
        } else {
            //有id的情况
            TravelRoute oldTravelRoute = getTravelRouteById(travelRoute.getId());
            travelRoute.setStatus(oldTravelRoute.getStatus());
            travelRoute.setCollectNumber(oldTravelRoute.getCollectNumber());
            travelRoute.setCreateDate(oldTravelRoute.getCreateDate());
        }
        travelRouteRepository.saveAndFlush(travelRoute);
        return ResultGenerator.genSuccessResult();
    }

    public TravelRoute getTravelRouteById(String id) {
        TravelRoute travelRoute = travelRouteRepository.findById(id).orElseThrow(() -> new ServiceException("路线ID错误!"));
        return travelRoute;
    }

    public Result updateTravelRouteStatus(String id) {
        TravelRoute travelRoute = getTravelRouteById(id);
        if (travelRoute.getStatus().equals(StatusEnum.DOWM_STATUS.getCode())) {
            //改变状态
            travelRoute.setStatus(StatusEnum.UP_STATUS.getCode());
        } else {
            travelRoute.setStatus(StatusEnum.DOWM_STATUS.getCode());
        }
        travelRouteRepository.saveAndFlush(travelRoute);
        return ResultGenerator.genSuccessResult();
    }

    public Page<TravelStrategy> getTravelStrategyPage(Pageable pageable) {
        Page<TravelStrategy> travelStrategyPage = travelStrategyRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.where(predicates.toArray(new Predicate[]{}));
            query.orderBy(cb.desc(root.get("createDate")));
            return null;
        }, pageable);
        return travelStrategyPage;
    }

    public TravelStrategy getTravelStrategyById(String id) {
        TravelStrategy travelStrategy = travelStrategyRepository.findById(id).orElseThrow(() -> new ServiceException("攻略ID错误!"));
        return travelStrategy;
    }

    public Result updateTravelStrategyStatus(String id) {
        TravelStrategy travelStrategy = getTravelStrategyById(id);
        if (travelStrategy.getStatus().equals(StatusEnum.DOWM_STATUS.getCode())) {
            //改变状态
            travelStrategy.setStatus(StatusEnum.UP_STATUS.getCode());
        } else {
            travelStrategy.setStatus(StatusEnum.DOWM_STATUS.getCode());
        }
        travelStrategyRepository.saveAndFlush(travelStrategy);
        return ResultGenerator.genSuccessResult();
    }


    @Transactional(rollbackFor = Exception.class)
    public Result saveTravelStrategy(HttpServletRequest request, TravelStrategy travelStrategy) {

        if (StringUtils.isEmpty(travelStrategy.getId())) {//没有id的情况
            travelStrategy.setId(IdGenerator.id());
            if (travelStrategy.getStatus() == null) {
                //默认为停用
                travelStrategy.setStatus(StatusEnum.DOWM_STATUS.getCode());
                travelStrategy.setCreateDate(new Date());
            }
        } else {
            //有id的情况
            TravelStrategy oldTravelStrategy = getTravelStrategyById(travelStrategy.getId());
            travelStrategy.setStatus(StatusEnum.Third_STATUS.getCode());
            travelStrategy.setCreateDate(oldTravelStrategy.getCreateDate());
            travelStrategy.setUser(oldTravelStrategy.getUser());
            travelStrategy.setTitle(oldTravelStrategy.getTitle());
            travelStrategy.setDescribe(oldTravelStrategy.getDescribe());
            travelStrategy.setErrorMessage(request.getParameter("errorMessage"));
        }
        travelStrategyRepository.saveAndFlush(travelStrategy);
        return ResultGenerator.genSuccessResult();
    }

    public Result exportHotel(HttpServletRequest request,HttpServletResponse response){
        List<String> column = new ArrayList<>();
        column.add("用户名");
        column.add("名字");
        column.add("预定的酒店名字");
        column.add("电话");
        column.add("性别");
        column.add("邮箱");
        List<Map<String,Object>> data = new ArrayList<>();
        List<UserHotel> list=userHotelRepository.findAll();
        //遍历获取到的需要导出的数据，k要和表头一样
        for (int i = 0; i < list.size(); i++) {
            Map<String,Object> dataMap = new HashMap<>();
            User user = userRepository.findById(list.get(i).getUser().getId()).get();
            dataMap.put("用户名",user.getId());
            dataMap.put("名字",user.getName());
            dataMap.put("预定的酒店名字",list.get(i).getHotel().getName());
            dataMap.put("电话",user.getPhone());
            dataMap.put("性别",user.getGender());
            dataMap.put("邮箱",user.getEmail());
            data.add(dataMap);
        }

//        List<Map<String,Object>> data = reserveService.getTop10Hotel();
        ExportExcel.exportExcel("酒店预定信息表",column,data,request,response);
        return ResultGenerator.genSuccessResult();
    }


}
