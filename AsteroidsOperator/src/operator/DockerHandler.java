/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import static com.github.dockerjava.api.model.HostConfig.newHostConfig;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import server.network.packets.MonitorPacket;

/**
 *
 * @author tomei
 */
public class DockerHandler {

    private DockerClient dockerClient;
    private String latestContainerId;

    public DockerHandler() {
        AsteroidsOperator.logger.log(INFO, "[DockerHandler] Create");
        dockerClient = DockerClientBuilder.getInstance()
                .build();
    }

    public void startServer() {
        AsteroidsOperator.logger.log(INFO, "[DockerHandler] Start server");
        dockerClient = DockerClientBuilder.getInstance()
                .build();
        HostConfig hostConfig = newHostConfig().withNetworkMode("host").withPublishAllPorts(true);
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd("server:latest")
                    .withHostConfig(hostConfig)
                    .exec();
            dockerClient.startContainerCmd(container.getId()).exec();
            latestContainerId = container.getId();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public StatsCallbackLog getContainerCpuUsage(String id) throws InterruptedException {
        dockerClient = DockerClientBuilder.getInstance()
                .build();
        StatsCallbackLog statsCallbackLog = new StatsCallbackLog();
        dockerClient.statsCmd(id).exec(statsCallbackLog);
        return statsCallbackLog;
    }

    public double calculateCPUPercent(Statistics statistics) throws NullPointerException {
        System.out.println("Cpu total: " + statistics.getCpuStats().getCpuUsage().getTotalUsage() + " PreCpu total: " + statistics.getPreCpuStats().getCpuUsage().getTotalUsage());
        double cpuDelta = statistics.getCpuStats().getCpuUsage().getTotalUsage() - statistics.getPreCpuStats().getCpuUsage().getTotalUsage();
        System.out.println("Cpu delta: " + cpuDelta);
        System.out.println("System usage: " + statistics.getCpuStats().getSystemCpuUsage() + " PreSystem usage: " + statistics.getPreCpuStats().getSystemCpuUsage());
        double systemDelta = statistics.getCpuStats().getSystemCpuUsage() - statistics.getPreCpuStats().getSystemCpuUsage();
        System.out.println("System delta: " + systemDelta);
        System.out.println("Cpu percentage: " + cpuDelta / systemDelta);
        return cpuDelta / systemDelta;
    }

    public String getLatestContainerId() {
        return latestContainerId;
    }

}
