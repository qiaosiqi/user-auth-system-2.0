package com.authority.backend.service.impl;

import com.authority.backend.entity.SysFunction;
import com.authority.backend.mapper.SysFunctionMapper;
import com.authority.backend.service.SysFunctionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysFunctionServiceImpl extends ServiceImpl<SysFunctionMapper, SysFunction> implements SysFunctionService {
}