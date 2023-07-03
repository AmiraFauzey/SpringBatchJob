package com.mira.springbatchjob.config;

import org.springframework.batch.item.ItemProcessor;

import com.mira.springbatchjob.entity.AccountInformation;

public class AccountInformationProcessor implements ItemProcessor<AccountInformation,AccountInformation>{

	@Override
	public AccountInformation process(AccountInformation item) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("item >>" + item);
		return item;
	}

}
