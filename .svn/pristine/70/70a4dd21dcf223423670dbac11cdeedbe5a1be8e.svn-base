package com.apip.forwarder.dao;



import com.acn.util.ApipPluginConstants;
import com.acn.util.PropertiesLoader;

import org.springframework.jdbc.core.JdbcTemplate;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: darora
 * Date: 5/15/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */

public class ApipPluginDaoImpl implements ApipPluginDao{
// I am extending JdbcDaoSupport class because ita allows me to get trid of boiler plate code getJdbcTemplate()

    private JdbcTemplate jdbcTemplate;


    private DataSource apipPluginDataSource;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    @Resource(name="apipPluginDataSource")
    public void setApipPluginDataSource(DataSource apipPluginDataSource) {
        this.apipPluginDataSource = apipPluginDataSource;
        this.jdbcTemplate = new JdbcTemplate(apipPluginDataSource);
    }

    @Override
    public String getRequestDataFromPaymentTxn(String txnId) {
        String schemaName = PropertiesLoader.getUrlFromPropertiesFile("schemaName", ApipPluginConstants.APIP_FWD_FILE_NAME);
        String sql = "Select request from "+schemaName+ ".PAYMENTTXN where TXNREFERENCENUMBER = ?";
        String requestColumnValue = this.getJdbcTemplate().queryForObject(sql, new Object[]{txnId}, String.class);
        return requestColumnValue;
    }
}
