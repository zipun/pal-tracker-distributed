package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<Long, ProjectInfo> concurrentMap;
    private final RestOperations restOperations;
    private final String endpoint;


    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.concurrentMap = new ConcurrentHashMap<>();
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }
    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        concurrentMap.put(projectId,projectInfo);
        return projectInfo;
    }

    public  ProjectInfo getProjectFromCache(long projectId){
        logger.info("Getting project with id {} from cache", projectId);
        return (ProjectInfo)concurrentMap.get(projectId);
    }
}
