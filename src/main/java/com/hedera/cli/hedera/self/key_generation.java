package com.hedera.cli.hedera.self;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Parameters;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;
import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@Command(name = "self",
         description = "@|fg(225) Transfer hbars to a single account|@%n",
         helpCommand = true)
public class KeyGeneration implements Runnable {
         
  @Autowired
  private Hedera hedera;

  @Autowired
  private ShellHelper shellHelper;

  @NonNull
  private List<String> mnemonic;
  private HGCSeed hgcSeed;

  

  @Override
  public void run() {
    System.out.println("KeyGeneration");
    shellHelper.print("Test_Process");
  }
}
