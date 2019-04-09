package com.nedap.university.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PacketBuilderTest.class, DownUploaderHandlerTest.class, DownUploaderTest.class, PacketReceiverTest.class, PacketSenderTest.class})
public class AllTests {

}
