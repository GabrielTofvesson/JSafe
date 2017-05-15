package net.tofvesson.jsafe.command;

import net.tofvesson.jsafe.JSafe;

import java.io.IOException;
import java.io.OutputStream;

public class ProcessInteractor {

    public ProcessInteractor(JSafe sandbox){

    }

    final class CommandStream extends OutputStream {

        final String enc;
        final char commandCode, notCommand;
        boolean commandMode = false;

        public CommandStream(char commandCode){
            this.commandCode = commandCode;
            notCommand = commandCode=='&'?'#':'&';
            enc = notCommand+"cmd;";
        }

        @Override
        public void write(int b) throws IOException {

        }

    }
}
