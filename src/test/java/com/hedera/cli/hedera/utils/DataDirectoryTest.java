package com.hedera.cli.hedera.utils;

import org.junit.Test;
import java.io.InputStream;

public class DataDirectoryTest {

    @Test
    public void testReadJsonToMap() {
        DataDirectory dataDirectory = new DataDirectory();
        InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");
        dataDirectory.readJsonToMap(addressBookInputStream);
    }

    @Test
    public void testMkHederaSubDir() {
        // This needs to be mocked.
//        String pathToSubDir = "aspen/files";
//        DataDirectory.mkHederaSubDir(pathToSubDir);

    }

    @Test
    public void testNetworkGetName() {
        DataDirectory dataDirectory = new DataDirectory();
        InputStream addressBookInputStream = getClass().getResourceAsStream("/addressbook.json");
        String networkName = dataDirectory.networkGetName(addressBookInputStream);
        System.out.println(networkName);
    }
}
