package com.vsthost.rnd.jribinding;

import org.rosuda.REngine.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides a singleton(ish) REngine binding and related convenience methods.
 */
public class RBinding {
    /**
     * Defines the version of the RBinding.
     */
    public static final String VERSION = "0.0.1";

    /**
     * Constructs an object representing an R Binding.
     *
     * @param args Arguments to R.
     * @param callbacks R Engine callback.
     * @throws RBindingBootstrapException Indicates that REngine could not be obtained properly.
     */
    private RBinding(String[] args, REngineCallbacks callbacks) throws RBindingBootstrapException {
        // Check if an REngine has been created before. If not,
        // attempt to create one and return it.
        if (REngine.getLastEngine() == null) {
            try {
                // Attempt to instantiate the REngine:
                REngine.engineForClass("org.rosuda.REngine.JRI.JRIEngine", args, callbacks, false);
            }
            catch (ClassNotFoundException e) {
                throw new RBindingBootstrapException("Cannot find engine class.", e);
            }
            catch (NoSuchMethodException e) {
                throw new RBindingBootstrapException("Can not create engine.", e);
            }
            catch (IllegalAccessException e) {
                throw new RBindingBootstrapException("Can not create engine.", e);
            }
            catch (InvocationTargetException e) {
                throw new RBindingBootstrapException("Can not create engine.", e);
            }
        }
    }

    /**
     * Returns the last engine.
     *
     * @return The last engine.
     */
    public REngine getREngine() {
        return REngine.getLastEngine();
    }

    /**
     * Parses and evaluates the given string.
     *
     * @param expression the expression to be parsed and evaluated.
     * @return the return value of the evaluation.
     * @throws RBindingParseAndEvalException Indicates that parsing and/or evaluating the R expression has failed.
     */
    public REXP evalExpr(String expression) throws RBindingParseAndEvalException {
        try {
            return this.getREngine().parseAndEval(expression);
        }
        catch (REngineException e) {
            throw new RBindingParseAndEvalException(expression, e);
        }
        catch (REXPMismatchException e) {
            throw new RBindingParseAndEvalException(expression, e);
        }
    }

    /**
     * Returns the R version.
     *
     * @return The R version.
     */
    public String getRVersion () {
        try {
            return this.evalExpr("version$version.string").asString();
        }
        catch (REXPMismatchException e) {
            e.printStackTrace();
        }
        catch (RBindingParseAndEvalException e) {
            e.printStackTrace();
        }
        return "(R version not available)";
    }

    /**
     * Parses, evaluates and assigns the given string to the given symbol.
     *
     * @param symbol     the symbol to assign to.
     * @param expression the expression to be parsed and evaluated.
     * @throws RBindingParseAndEvalException Indicates that parsing and/or evaluating the R expression has failed.
     */
    public void assignEvalExpr(String symbol, String expression) throws RBindingParseAndEvalException {
        this.evalExpr(symbol + " <- eval(parse(text=\"" + expression.replace("\"", "\\\"") + "\"))");
    }

    /**
     * Loads a library provided as a string.
     *
     * @param libraryName Defines the name of the library to be loaded.
     * @throws RBindingLibraryLoadException Indicates that the library could not be loaded properly.
     */
    public void loadLibrary(String libraryName) throws RBindingLibraryLoadException {
        try {
            this.evalExpr("library(" + libraryName + ")");
        }
        catch (RBindingParseAndEvalException e) {
            throw new RBindingLibraryLoadException(libraryName, e);
        }
    }

    /**
     * Loads a script of which its path provided as a string.
     *
     * @param scriptPath Defines the path of the source to be loaded.
     * @throws RBindingScriptLoadException Indicates that the script could not be sourced properly.
     */
    public void loadScript(String scriptPath) throws RBindingScriptLoadException {
        try {
            this.evalExpr("source(\"" + scriptPath + "\")");
        }
        catch (RBindingParseAndEvalException e) {
            throw new RBindingScriptLoadException(scriptPath, e);
        }
    }

    /**
     * Closes the REngine if any available.
     *
     * <p>Note that after this things are going to become nasty as it is not possible to open a new one with JRI backend.
     * Make sure that you restart your application.</p>
     */
    public static void closeREngine() {
        // Check if any REngine has been initialized yet:
        if (REngine.getLastEngine() != null) {
            // Close the REngine:
            REngine.getLastEngine().close();
        }
    }

    /**
     * Returns an R binding with an R engine callback attached.
     *
     * @param callbacks The R engine callback to attach.
     * @return An RBinding instance.
     * @throws RBindingBootstrapException Indicates that REngine could not be obtained properly.
     */
    public static synchronized RBinding getInstance(REngineCallbacks callbacks) throws RBindingBootstrapException {
        return new RBinding(new String[] {"--vanilla", "--quiet"}, callbacks);
    }

    /**
     * Returns an R binding with standard output from R disabled.
     *
     * @return An RBinding instance.
     * @throws RBindingBootstrapException Indicates that REngine could not be obtained properly.
     */
    public static synchronized RBinding getInstance() throws RBindingBootstrapException {
        return RBinding.getInstance(false);
    }

    /**
     * Returns an R binding with standard output from R enabled or disabled.
     *
     * @param stdout Indicates if standard output from R is enabled or disabled.
     * @return An RBinding instance.
     * @throws RBindingBootstrapException Indicates that REngine could not be obtained properly.
     */
    public static synchronized RBinding getInstance(boolean stdout) throws RBindingBootstrapException {
        return RBinding.getInstance(stdout ? new REngineStdOutput() : null);
    }
}