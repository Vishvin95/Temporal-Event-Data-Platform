package com.temporal.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.temporal.persistence.DescribeBuilder;
import com.temporal.persistence.Excecutor;

public class DescribeQuery {
	public static ResultSet getDomainInformation(String domainName)
	{
		DescribeBuilder sb = new DescribeBuilder(domainName);
		Excecutor e = new Excecutor();
		e.addSqlQuery(sb);
		List<ResultSet> l = null;
		try {
			l = e.execute();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return l.get(0);
	}
}
