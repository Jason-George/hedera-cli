package com.hedera.cli.commands;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.shell.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class HederaVersion extends CliDefaults {

    @Value("${app.version}")
    private String version;

    @Value("${app.name}")
    private String name;

    @Value("${app.licenseYear}")
    private String licenseYear;

    @Autowired
    ShellHelper shellHelper;

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "hedera-cli version")
    public void version() {
        shellHelper.printInfo(version);
    }
}
