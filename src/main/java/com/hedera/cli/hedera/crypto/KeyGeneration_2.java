package com.hedera.cli.hedera.crypto;

import com.hedera.cli.hedera.bip39.Mnemonic;
import com.hedera.cli.hedera.bip39.MnemonicException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;

import lombok.NoArgsConstructor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Parameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;
import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@Component
@Command(name = "generate",
         description = "@|fg(225) Transfer hbars to a single account|@%n",
         helpCommand = true)
public class KeyGeneration_2 implements Runnable,Operation {
         
         
     @Autowired
     private Hedera hedera;

     @Autowired
     private ShellHelper shellHelper;

     ///private List<String> mnemonic;
     //private HGCSeed hgcSeed;

     @Parameters(index = "0", description = "working on it")

     @Override
     public void run() {
              
      System.out.println("KeyGeneration");
    //hgcSeed = new HGCSeed(CryptoUtils.getSecureRandomData(32));
    //mnemonic = generateMnemonic(hgcSeed);
    //generateKeysAndWords(hgcSeed, mnemonic);
  }
         
     @Override
      public void executeSubCommand(InputReader inputReader, String... args) {
         if (args.length == 0) {
            CommandLine.usage(this, System.out);
        } else {
            try {
                new CommandLine(this).execute(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
