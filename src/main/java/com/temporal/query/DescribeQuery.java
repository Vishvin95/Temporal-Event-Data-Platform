package com.temporal.query;

import java.sql.ResultSet;
import java.util.List;

import com.temporal.persistence.DescribeBuilder;
import com.temporal.persistence.Excecutor;

public class DescribeQuery {
	public static ResultSet getDomainInformation(String domainName)
	{
		DescribeBuilder sb = new DescribeBuilder(domainName);
		Excecutor e = new Excecutor();
		e.addSqlQuery(sb);
		List<ResultSet> l = e.execute();
		return l.get(0);
	}
}
