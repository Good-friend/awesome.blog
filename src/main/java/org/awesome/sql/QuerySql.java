package org.awesome.sql;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class QuerySql {



    public String queryFirstPageSql(Map<String,String> params) {
        return new SQL() {
            {
                SELECT ("t.*,t1.nickname,t1.head_portrait_url,t2.type_name");

                FROM("t_catalogue t , t_user t1 , t_type t2 ")  ;

                WHERE("t.author = t1.username and t.type = t2.type_id ");

                if(params.get("type") !=null){
                    WHERE("t.type =#{type}");
                }
                if(params.get("status") !=null){
                    WHERE("t.status =#{status}");
                }
                if(params.get("best") !=null){
                    WHERE("t.best =#{best}");
                }
                if("heat".equals(params.get("orderType"))){
                    ORDER_BY("t.comment_times desc");
                }else{
                    ORDER_BY("t.id desc");
                }



            }
        }.toString();
    }


    public String queryConnotationDetailSql(String serialNumber) {
        return new SQL() {
            {
                SELECT("t.*,t1.nickname,t1.head_portrait_url,t2.type_name,t3.content ");

                FROM("t_catalogue t , t_user t1 , t_type t2,t_connotation t3 ");

                WHERE("t.author = t1.username and t.type = t2.type_id and t.serial_number = t3.serial_number and t.serial_number='" + serialNumber + "'");

            }
        }.toString();
    }
}
