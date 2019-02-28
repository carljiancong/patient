package com.harmonycloud.controller;

import com.harmonycloud.result.CodeMsg;
import com.harmonycloud.result.Result;
import com.harmonycloud.service.PatientService;
import com.harmonycloud.vo.CpVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
@Api(tags = "Patient")
@RestController
public class PatientController {

    @Resource
    private PatientService patientService;


    @ApiOperation(value = "register patient", httpMethod = "POST")
    @ApiImplicitParam(name = "cpVo", value = "patient entity", dataType = "CpVo")
    @PostMapping("/register")
    public Result register(@RequestBody CpVo cpVo) {
        return patientService.register(cpVo);
    }

    @ApiOperation(value = "get patient list", httpMethod = "POST")
    @ApiImplicitParam(name = "param", value = "{\"searchData\":\"1\"}", required = true, dataType = "Map")
    @PostMapping("/searchPatient")
    public Result getPatientList(@RequestBody Map<String,String> param) {
        String searchData = param.get("searchData");
        if (StringUtils.isEmpty(searchData)) {
            return Result.buildError(CodeMsg.PATIENT_NOT_EXIST);
        }
        return patientService.getPatientList(searchData);
    }

    @ApiOperation(value = "update patient", httpMethod = "POST")
    @ApiImplicitParam(name = "cpVo", value = "patient and person", required = true, dataType = "CpVo")
    @PostMapping("/update")
    public Result updatePatient(@RequestBody CpVo cpVo) {
        return patientService.updatePatient(cpVo);
    }
}
