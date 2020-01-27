package com.hedera.cli.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.hcs.Consensus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaConsensus extends CliDefaults {

  @Autowired
  private Consensus consensus;

  @ShellMethod(value = "manage Hedera consensus service") // @formatter:off
  public void hcs(@ShellOption(defaultValue = "") String subCommand,
                  @ShellOption(defaultValue = "") String topicIdString,
                  @ShellOption(value = {"-m", "--memo"}, defaultValue = "") String m,
                  @ShellOption(value = {"-k", "--submitKey"}, defaultValue = "") String k,
                  // Specifying -y flag will set y to be true (which will require submit key)
                  @ShellOption(value = {"-y", "--yes"}, arity = 0, defaultValue = "false") boolean y
  ) { // @formatter:on

    String[] args;
    List<String> argsList = new ArrayList<>();
    Object[] objs;

    switch (subCommand) {
    case "create":
      if (!m.isEmpty()) argsList.add("-m " + m);
      if (!k.isEmpty()) argsList.add("-k " + k);
      argsList.add("-y=" + y);
      break;
    case "submit":
      argsList = addToArgsList(topicIdString, argsList);
      if (!m.isEmpty()) argsList.add("-m " + m);
      if (!k.isEmpty()) argsList.add("-k " + k);
      break;
    case "read":
      System.out.println("To subscribe to hcs messages, run");
      System.out.println("hedera -S [topic id]");
      System.out.println("in a separate terminal pane and not in our hedera REPL environment");
      return;
    default:
      break;
    }
    objs = argsList.toArray();
    args = Arrays.copyOf(objs, objs.length, String[].class);
    consensus.handle(subCommand, args);
  }

  public List<String> addToArgsList(String topicIdString, List<String> argsList) {
    if (!topicIdString.isEmpty()) {
      argsList.add(topicIdString);
      return argsList;
    } else {
      return argsList;
    }
  }
}
