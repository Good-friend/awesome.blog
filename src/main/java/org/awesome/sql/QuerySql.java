package org.awesome.sql;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import java.util.Map;

public class QuerySql {



    public String queryFirstPageSql(Map<String,String> params) {
        return new SQL() {
            {
                SELECT ("t.*,t1.nickname,t1.head_portrait_url,t2.type_name");

                FROM("t_catalogue t , t_user t1 , t_type t2 ")  ;

                WHERE("t.author = t1.username and t.type = t2.type_id and t.publicity = '1' ");


                if(params.get("stick") !=null){
                    WHERE("t.stick =#{stick}");
                }
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
                    String sql = "t.comment_times desc";
                    if(params.get("queryCount") !=null){
                        sql += " limit "+params.get("queryCount");
                    }else{
                        sql += " limit 0,15";
                    }
                    ORDER_BY(sql);
                }else{
                    String sql = "t.id desc";
                    if(params.get("queryCount") !=null){
                        sql += " limit "+params.get("queryCount");
                    }else{
                        sql += " limit 0,15";
                    }
                    ORDER_BY(sql);
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


    public String queryCatalogueByParams(Map<String,String> params) {
        return new SQL() {
            {
                SELECT("t.*");

                FROM("t_catalogue t ");

                if(!StringUtils.isEmpty(params.get("username"))){

                    WHERE("author = #{username} ");

                    if(!StringUtils.isEmpty(params.get("lookOther"))){

                        WHERE("stick = '0' and publicity = '1'");

                    }
                }else{

                    WHERE("stick = '0' and publicity = '1' ");

                }

                if("heat".equals(params.get("orderType"))){

                    ORDER_BY("t.comment_times desc limit 0, 10");

                }else{

                    ORDER_BY("t.id desc limit 0,10");

                }

            }
        }.toString();

    }

    public String countCatalogueAuthor(Map<String,String> params){
        return new SQL() {
            {
                SELECT("t.nickname,t.username,t.head_portrait_url,IFNULL(t1.cataCount,0) as cataCount,IFNULL(t2.favorCount,0) as favorCount");
                FROM("t_user t");
                if(!StringUtils.isEmpty(params.get("publicity"))){
                    LEFT_OUTER_JOIN("(select t.author,count(*) as cataCount from t_catalogue t where t.publicity=#{publicity} GROUP BY t.author) t1 ON t.username = t1.author");
                }else{
                    LEFT_OUTER_JOIN("(select t.author,count(*) as cataCount from t_catalogue t GROUP BY  t.author) t1 ON t.username = t1.author");
                }
                LEFT_OUTER_JOIN("(select t.username,count(*) as favorCount from t_favorite t GROUP BY t.username) t2 ON t.username = t2.username");
                //WHERE("t.username = t1.author and t.username = t2.username");
                if(!StringUtils.isEmpty(params.get("username"))){
                    WHERE("t.username = #{username}");
                }
                ORDER_BY("t1.cataCount DESC LIMIT 0,4");
            }
        }.toString();
    }


    public String getFavoritesByUsername(String username) {
        return new SQL() {
            {
                SELECT("t1.id as favoriteId,t.serial_number as serialNumber,t.title,t1.create_time as createTime ");
                FROM("t_catalogue t,t_favorite t1");
                WHERE("t.serial_number = t1.serial_number and t1.username = #{username}");
            }
        }.toString();
    }
}
