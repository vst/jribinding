package com.vsthost.rnd;

import com.vsthost.rnd.jribinding.RBinding;
import com.vsthost.rnd.jribinding.RBindingBootstrapException;
import com.vsthost.rnd.jribinding.RBindingScriptLoadException;

/**
 * Provides a command line interface to R bindings.
 */
public class RBindingCLI {
    public static void main( String[] args ) throws RBindingBootstrapException, RBindingScriptLoadException {
        // Get the R binding instance:
        final RBinding r = RBinding.getInstance(true);

        // Display preamble:
        System.out.println(String.format("jribinding version %s to %s\n", RBinding.VERSION, r.getRVersion()));

        // If any source files provided, source them in R:
        if (args.length > 0) {
            r.loadScript(args[0]);
        }

        // Disconnect R:
        RBinding.closeREngine();
    }
}
