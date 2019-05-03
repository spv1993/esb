package edu.servicemix.esb.adapters.events.mybatis.typehandlers;

import org.apache.cxf.jaxb.DatatypeFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import javax.xml.datatype.Duration;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

@SuppressWarnings("PackageAccessibility")
public class DurationTypeHandler implements TypeHandler<Duration> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Duration parameter,
			JdbcType jdbcType) throws SQLException {

		ps.setString(i, parameter.toString());
	}

	@Override
	public Duration getResult(ResultSet rs, String columnName)
			throws SQLException {

		String formattedString = java.time.Duration.between(LocalTime.MIN, 
				LocalTime.parse(rs.getString(columnName))).toString();
		
		return DatatypeFactory.createDuration(formattedString);
	}

	@Override
	public Duration getResult(ResultSet rs, int columnIndex)
			throws SQLException {
		
		String formattedString = java.time.Duration.between(LocalTime.MIN, 
				LocalTime.parse(rs.getString(columnIndex))).toString();
		
		return DatatypeFactory.createDuration(formattedString);
	}

	@Override
	public Duration getResult(CallableStatement cs, int columnIndex)
			throws SQLException {

		String formattedString = java.time.Duration.between(LocalTime.MIN, 
				LocalTime.parse(cs.getString(columnIndex))).toString();
		
		return DatatypeFactory.createDuration(formattedString);
	}

}
