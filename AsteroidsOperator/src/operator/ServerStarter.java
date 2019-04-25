/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import static com.github.dockerjava.api.model.HostConfig.newHostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;

/**
 *
 * @author tomei
 */
public class ServerStarter extends Thread {

    private String latestContainerId;
    private static final boolean useDocker = false;

    public ServerStarter() {
        AsteroidsOperator.logger.log(FINE, "[ServerStarter] Create");
    }

    private String[] getCommand() {
        if (useDocker) {
            return new String[]{"docker", "run", "--cpus=1.0", "--rm", "--net=host", "-P", "server:latest"};
        } else {
            return new String[]{"java", "-jar", "lib/AsteroidsServer.jar", "3200", "3200", "32", "127.0.1.1", "8851"};
        }
    }

    @Override
    public void run() {
        AsteroidsOperator.logger.log(INFO, "[ServerStarter] Start");
        String[] command = getCommand();
        System.out.println("Starting new server");
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println("Command: " + String.join(" ", command));
        ProcessBuilder probuilder = new ProcessBuilder(command);

        Process process;
        try {
            process = probuilder.start();
            int status = process.waitFor();
            AsteroidsOperator.logger.log(INFO, "[ServerStarter] Exited with status: {0}", status);
        } catch (InterruptedException e) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerStarter] Server process interupted");
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerStarter] Server process failed");
        }
    }

    public void startServer() {
        /*DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        List<Container> containers = dockerClient.listContainersCmd().exec();
        for (com.github.dockerjava.api.model.Container container : containers) {
            System.out.println(container.toString());
        }

        CreateContainerResponse container = dockerClient.createContainerCmd("server:latest")
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
         */
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        CreateContainerResponse container = dockerClient.createContainerCmd("server:latest")
                .withHostConfig(newHostConfig()
                        .withNetworkMode("host").withPublishAllPorts(true))
                .exec();

        dockerClient.startContainerCmd(container.getId()).exec();
        latestContainerId = container.getId();
    }    
    
    
    
    public void startServer2() {

        try {
            URL url = new URL("http://127.0.0.1:2375");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            Map<String, String> parameters = new HashMap<>();
            parameters.put("Image", "server:latest");
            parameters.put("NetworkMode", "host");
            parameters.put("PublishAllPorts", "true");

            int responseCode = con.getResponseCode();
            System.out.println("Sending request to " + url);
            System.out.println("Response code = " + responseCode);

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            System.out.println("Output of request: " + content);
        } catch (IOException ex) {
            Logger.getLogger(ServerStarter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getLatestContainerId() {
        return latestContainerId;
    }



}
