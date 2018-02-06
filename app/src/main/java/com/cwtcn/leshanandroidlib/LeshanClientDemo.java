/*******************************************************************************
 * Copyright (c) 2013-2015 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Zebra Technologies - initial API and implementation
 *     Sierra Wireless, - initial API and implementation
 *     Bosch Software Innovations GmbH, - initial API and implementation
 *******************************************************************************/

package com.cwtcn.leshanandroidlib;

import android.content.Context;
import android.util.Log;

import com.cwtcn.leshanandroidlib.resources.MyDevice;
import com.cwtcn.leshanandroidlib.resources.MyLocation;
import com.cwtcn.leshanandroidlib.resources.RandomTemperatureSensor;

import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.observer.LwM2mClientObserver;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.request.BindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.eclipse.leshan.LwM2mId.DEVICE;
import static org.eclipse.leshan.LwM2mId.LOCATION;
import static org.eclipse.leshan.LwM2mId.SECURITY;
import static org.eclipse.leshan.LwM2mId.SERVER;
import static org.eclipse.leshan.client.object.Security.noSec;
import static org.eclipse.leshan.client.object.Security.noSecBootstap;
import static org.eclipse.leshan.client.object.Security.psk;
import static org.eclipse.leshan.client.object.Security.pskBootstrap;

//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.DefaultParser;
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.ParseException;

public class LeshanClientDemo {

    private static final String TAG = "LeshanClientDemo";
    private static final Logger LOG = LoggerFactory.getLogger(LeshanClientDemo.class);

    private final static String[] modelPaths = new String[] { "3303.xml" };

    private static final int OBJECT_ID_TEMPERATURE_SENSOR = 3303;
    private final static String DEFAULT_ENDPOINT = "LeshanClientDemo";
    private final static String USAGE = "java -jar leshan-client-demo.jar [OPTION]";

    private MyLocation locationInstance;
    private LeshanClient mClient;
    private Context mContext;
    private LwM2mClientObserver mObserver;
    public LeshanClientDemo(Context context) {
        this.mContext = context;
    }

    public void startClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                main();
            }
        }).start();
    }
    public void main() {
            Log.d(TAG, "main == >");
//        // Define options for command line tools
//        Options options = new Options();
//
//        options.addOption("h", "help", false, "Display help information.");
//        options.addOption("n", true, String.format(
//                "Set the endpoint name of the Client.\nDefault: the local hostname or '%s' if any.", DEFAULT_ENDPOINT));
//        options.addOption("b", false, "If present use bootstrap.");
//        options.addOption("lh", true, "Set the local CoAP address of the Client.\n  Default: any local address.");
//        options.addOption("lp", true,
//                "Set the local CoAP port of the Client.\n  Default: A valid port value is between 0 and 65535.");
//        options.addOption("slh", true, "Set the secure local CoAP address of the Client.\nDefault: any local address.");
//        options.addOption("slp", true,s
//                "Set the secure local CoAP port of the Client.\nDefault: A valid port value is between 0 and 65535.");
//        options.addOption("u", true, String.format("Set the LWM2M or Bootstrap server URL.\nDefault: localhost:%d.",
//                LwM2m.DEFAULT_COAP_PORT));
//        options.addOption("i", true,
//                "Set the LWM2M or Bootstrap server PSK identity in ascii.\nUse none secure mode if not set.");
//        options.addOption("p", true,
//                "Set the LWM2M or Bootstrap server Pre-Shared-Key in hexa.\nUse none secure mode if not set.");
//        options.addOption("pos", true,
//                "Set the initial location (latitude, longitude) of the device to be reported by the Location object. Format: lat_float:long_float");
//        options.addOption("sf", true, "Scale factor to apply when shifting position. Default is 1.0.");
//        HelpFormatter formatter = new HelpFormatter();
//        formatter.setOptionComparator(null);
//
//        // Parse arguments
//        CommandLine cl;
//        try {
//            cl = new DefaultParser().parse(options, args);
//        } catch (ParseException e) {
//            System.err.println("Parsing failed.  Reason: " + e.getMessage());
//            formatter.printHelp(USAGE, options);
//            return;
//        }
//
//        // Print help
//        if (cl.hasOption("help")) {
//            formatter.printHelp(USAGE, options);
//            return;
//        }
//
//        // Abort if unexpected options
//        if (cl.getArgs().length > 0) {
//            System.err.println("Unexpected option or arguments : " + cl.getArgList());
//            formatter.printHelp(USAGE, options);
//            return;
//        }
//
//        // Abort if we have not identity and key for psk.
//        if ((cl.hasOption("i") && !cl.hasOption("p")) || !cl.hasOption("i") && cl.hasOption("p")) {
//            System.err.println("You should precise identity and Pre-Shared-Key if you want to connect in PSK");
//            formatter.printHelp(USAGE, options);
//            return;
//        }

        // Get endpoint name
        String endpoint;
//        if (cl.hasOption("n")) {
//            endpoint = cl.getOptionValue("n");
//        } else {
            try {
                endpoint = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                endpoint = DEFAULT_ENDPOINT;
            }
//        }

        // Get server URI
        String serverURI;
//        if (cl.hasOption("u")) {
//            if (cl.hasOption("i"))
//                serverURI = "coaps://" + cl.getOptionValue("u");
//            else
//                serverURI = "coap://" + cl.getOptionValue("u");
//        } else {
//            if (cl.hasOption("i"))
//                serverURI = "coaps://localhost:" + LwM2m.DEFAULT_COAP_SECURE_PORT;
//            else
//                  serverURI = "coap://localhost:" + LwM2m.DEFAULT_COAP_PORT;
//        }
        serverURI = "coap://10.0.2.2:5484"; //+ LwM2m.DEFAULT_COAP_PORT;

        // get security info
        byte[] pskIdentity = null;
        byte[] pskKey = null;
//        if (cl.hasOption("i") && cl.hasOption("p")) {
//            pskIdentity = cl.getOptionValue("i").getBytes();
//            pskKey = Hex.decodeHex(cl.getOptionValue("p").toCharArray());
//        }

        // get local address
        String localAddress = null;
        int localPort = 0;
//        if (cl.hasOption("lh")) {
//            localAddress = cl.getOptionValue("lh");
//        }
//        if (cl.hasOption("lp")) {
//            localPort = Integer.parseInt(cl.getOptionValue("lp"));
//        }

        // get secure local address
        String secureLocalAddress = null;
        int secureLocalPort = 0;
//        if (cl.hasOption("slh")) {
//            secureLocalAddress = cl.getOptionValue("slh");
//        }
//        if (cl.hasOption("slp")) {
//            secureLocalPort = Integer.parseInt(cl.getOptionValue("slp"));
//        }

        Float latitude = null;
        Float longitude = null;
        Float scaleFactor = 1.0f;
        // get initial Location
//        if (cl.hasOption("pos")) {
//            try {
//                String pos = cl.getOptionValue("pos");
//                int colon = pos.indexOf(':');
//                if (colon == -1 || colon == 0 || colon == pos.length() - 1) {
//                    System.err.println("Position must be a set of two floats separated by a colon, e.g. 48.131:11.459");
//                    formatter.printHelp(USAGE, options);
//                    return;
//                }
//                latitude = Float.valueOf(pos.substring(0, colon));
//                longitude = Float.valueOf(pos.substring(colon + 1));
//            } catch (NumberFormatException e) {
//                System.err.println("Position must be a set of two floats separated by a colon, e.g. 48.131:11.459");
//                formatter.printHelp(USAGE, options);
//                return;
//            }
//        }
//        if (cl.hasOption("sf")) {
//            try {
//                scaleFactor = Float.valueOf(cl.getOptionValue("sf"));
//            } catch (NumberFormatException e) {
//                System.err.println("Scale factor must be a float, e.g. 1.0 or 0.01");
//                formatter.printHelp(USAGE, options);
//                return;
//            }
//        }

        createAndStartClient(endpoint, localAddress, localPort, secureLocalAddress, secureLocalPort, false,
                serverURI, pskIdentity, pskKey, latitude, longitude, scaleFactor);
    }

    public void createAndStartClient(String endpoint, String localAddress, int localPort,
            String secureLocalAddress, int secureLocalPort, boolean needBootstrap, String serverURI, byte[] pskIdentity,
            byte[] pskKey, Float latitude, Float longitude, float scaleFactor) {

        Log.d(TAG, "createAndStartClient == >");
        System.out.println("leshan.LeshanClientDemo.createAndStartClient  endpoint = " + endpoint
                + ", localAddress =" + localAddress
                + ", localPort = " + localPort
                + ", secureLocalAddress = " + secureLocalAddress
                + ", secureLocalPort = " + secureLocalPort
                + ", needBootstrap = " + needBootstrap
                + ", serverURI = " + serverURI);

//        endpoint = Blue-PC, localAddress
//                =null, localPort = 0, secureLocalAddress = null, secureLocalPort = 0, needBoots
//        trap = false, serverURI = coap://localhost:5683

        endpoint = "Blue-Phone";
        locationInstance = new MyLocation();

        // Initialize model
        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/assets", modelPaths));

        // Initialize object list
        ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(models));
        if (needBootstrap) {
            if (pskIdentity == null)
                initializer.setInstancesForObject(SECURITY, noSecBootstap(serverURI));
            else
                initializer.setInstancesForObject(SECURITY, pskBootstrap(serverURI, pskIdentity, pskKey));
        } else {
            if (pskIdentity == null) {
                initializer.setInstancesForObject(SECURITY, noSec(serverURI, 123));
                initializer.setInstancesForObject(SERVER, new Server(123, 30, BindingMode.U, false));
            } else {
                initializer.setInstancesForObject(SECURITY, psk(serverURI, 123, pskIdentity, pskKey));
                initializer.setInstancesForObject(SERVER, new Server(123, 30, BindingMode.U, false));
            }
        }
        initializer.setClassForObject(DEVICE, MyDevice.class);
        initializer.setInstancesForObject(LOCATION, locationInstance);
        initializer.setInstancesForObject(OBJECT_ID_TEMPERATURE_SENSOR, new RandomTemperatureSensor());
        List<LwM2mObjectEnabler> enablers = initializer.create(SECURITY, SERVER, DEVICE, LOCATION,
                OBJECT_ID_TEMPERATURE_SENSOR);


        // Create CoAP Config
        NetworkConfig coapConfig = null;
        try {
            InputStream inputStream = mContext.getResources().getAssets().open(NetworkConfig.DEFAULT_FILE_NAME);
            System.out.println("leshan.LeshanClientDemo.createAndStartClient inputStream = " + inputStream);
            //readProperties(inputStream);
//            System.out.println("leshan.LeshanClientDemo.createAndStartClient configFile.isFile(): " + configFile.isFile());
//            if (configFile.isFile()) {
                coapConfig = new NetworkConfig();
                coapConfig.load(inputStream);
//            } else {
//                coapConfig = LeshanClientBuilder.createDefaultNetworkConfig();
//                coapConfig.store(configFile);
//            }
        } catch (IOException e) {
            Log.d("zhiheng", "leshan get coapConfig input stream error");
            e.printStackTrace();
        }

        // Create client
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        builder.setLocalAddress(localAddress, localPort);
        builder.setLocalSecureAddress(secureLocalAddress, secureLocalPort);
        builder.setObjects(enablers);
        builder.setCoapConfig(coapConfig);
        // if we don't use bootstrap, client will always use the same unique endpoint
        // so we can disable the other one.
        if (!needBootstrap) {
            if (pskIdentity == null)
                builder.disableSecuredEndpoint();
            else
                builder.disableUnsecuredEndpoint();
        }
        mClient = builder.build();
        mClient.addObserver(mObserver);

//        LOG.info("Press 'w','a','s','d' to change reported Location ({},{}).", locationInstance.getLatitude(),
//                locationInstance.getLongitude());

        System.out.println("leshan.LeshanClientDemo.createAndStartClient  coap://" + mClient.getUnsecuredAddress().toString() + ", coaps://" + mClient.getSecuredAddress());

        System.out.println("leshan.LeshanClientDemo.endpoint = " + endpoint
    			+ ", localAddress =" + localAddress
    			+ ", localPort = " + localPort
    			+ ", secureLocalAddress = " + secureLocalAddress
    			+ ", secureLocalPort = " + secureLocalPort
    			+ ", needBootstrap = " + needBootstrap
    			+ ", serverURI = " + serverURI);
        // Start the client
        mClient.start();

        // De-register on shutdown and stop client.
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                mClient.destroy(true); // send de-registration request before destroy
//            }
//        });

        // Change the location through the Console
//        try (Scanner scanner = new Scanner(System.in)) {
//            while (scanner.hasNext()) {
//                String nextMove = scanner.next();
//                locationInstance.moveLocation(nextMove);
//            }
//        }
    }

    /**
     * 用于测试读取califonium.properties文件是否成功
     * @param inputStream
     */
    private void readProperties(InputStream inputStream) {
        int count = 0;
        InputStreamReader reader = new InputStreamReader(inputStream);
        char[] buf = new char[2048];
        try {
            int n = reader.read(buf, count, 1024);
            if (n > 0) {
                count += n;
            }
            Log.d("zhiheng", new String(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *  send de-registration request before destroy
     */
    public void stopClient() {
        mClient.destroy(true);
    }

    public void setObserver(LwM2mClientObserver observer) {
        this.mObserver = observer;
    }

}
