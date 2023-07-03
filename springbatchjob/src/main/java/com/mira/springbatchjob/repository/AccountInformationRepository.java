package com.mira.springbatchjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mira.springbatchjob.entity.AccountInformation;

@Repository
public interface AccountInformationRepository extends JpaRepository<AccountInformation,Integer>{

}
