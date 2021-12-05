package cn.xavier.service;

import java.util.List;
import java.util.Map;

/**
 * @author Zheng-Wei Shui
 * @date 12/5/2021
 */
public interface IDiffCheckService {
    /**
     * Gets all tables *
     *
     * @return the all tables
     */
    List<String> getAllTables();

    /**
     * 两表列的Diff check
     *
     * @param params params
     * @return the map
     */
    Map<String,String> diffCheck(Map<String, String> params);
}
