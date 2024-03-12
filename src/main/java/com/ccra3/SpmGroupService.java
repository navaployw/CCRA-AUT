package com.ccra3;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SpmGroupService {

    
    private SpmGroupRepository spmGroupRepository;

    public SpmGroup getAIFromAICode(String aiCode) throws Exception {

        SpmGroup groupObj = spmGroupRepository.findByaiCode(aiCode);
        return groupObj;
        
    }
}
