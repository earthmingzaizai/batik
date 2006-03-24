/*

   Copyright 2002-2005  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.jar.Manifest;

import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.ScriptEventWrapper;
import org.apache.batik.script.ScriptHandler;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.EventListenerInitializer;

/**
 * This class is the base class for SVG scripting.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class BaseScriptingEnvironment {
    /**
     * Constant used to describe inline scripts.
     * <pre>
     * {0} - URL of document containing script.
     * {1} - Element tag
     * {2} - line number of element.
     * </pre>
     */
    public static final String INLINE_SCRIPT_DESCRIPTION
        = "BaseScriptingEnvironment.constant.inline.script.description";

    /**
     * Constant used to describe inline scripts.
     * <pre>
     * {0} - URL of document containing script.
     * {1} - Event attribute name
     * {2} - line number of element.
     * </pre>
     */
    public static final String EVENT_SCRIPT_DESCRIPTION
        = "BaseScriptingEnvironment.constant.event.script.description";

    /**
     * Tells whether the given SVG document is dynamic.
     */
    public static boolean isDynamicDocument(BridgeContext ctx, Document doc) {
        Element elt = doc.getDocumentElement();
        if ((elt != null) &&
            SVGConstants.SVG_NAMESPACE_URI.equals(elt.getNamespaceURI())) {
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONABORT_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONERROR_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONRESIZE_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONUNLOAD_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONSCROLL_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONZOOM_ATTRIBUTE).length() > 0) {
                return true;
            }
            return isDynamicElement(ctx, doc.getDocumentElement());
        }
        return false;
    }

    public static boolean isDynamicElement(BridgeContext ctx, Element elt) {
        List bridgeExtensions = ctx.getBridgeExtensions(elt.getOwnerDocument());
        return isDynamicElement(elt, ctx, bridgeExtensions);
    }

    /**
     * Tells whether the given SVG element is dynamic.
     */
    public static boolean isDynamicElement
        (Element elt, BridgeContext ctx, List bridgeExtensions) {
        Iterator i = bridgeExtensions.iterator();
        while (i.hasNext()) {
            BridgeExtension bridgeExtension = (BridgeExtension) i.next();
            if (bridgeExtension.isDynamicElement(elt)) {
                return true;
            }
        }
        if (SVGConstants.SVG_NAMESPACE_URI.equals(elt.getNamespaceURI())) {
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONKEYUP_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONKEYDOWN_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONKEYPRESS_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONLOAD_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONERROR_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONACTIVATE_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONCLICK_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONFOCUSIN_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONFOCUSOUT_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEDOWN_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEMOVE_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEOUT_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEOVER_ATTRIBUTE).length() > 0) {
                return true;
            }
            if (elt.getAttributeNS
                (null, SVGConstants.SVG_ONMOUSEUP_ATTRIBUTE).length() > 0) {
                return true;
            }
        }

        for (Node n = elt.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (isDynamicElement(ctx, (Element)n)) {
                    return true;
                }
            }
        }
        return false;
    }


    protected final static String EVENT_NAME = "event";
    protected final static String ALTERNATE_EVENT_NAME = "evt";

    /**
     * The bridge context.
     */
    protected BridgeContext bridgeContext;

    /**
     * The user-agent.
     */
    protected UserAgent userAgent;

    /**
     * The document to manage.
     */
    protected Document document;

    /**
     * The URL of the document ot manage
     */
    protected ParsedURL docPURL;

    protected Set languages = new HashSet();

    /**
     * The default Interpreter for the document
     */
    protected Interpreter interpreter;

    /**
     * Creates a new BaseScriptingEnvironment.
     * @param ctx the bridge context
     */
    public BaseScriptingEnvironment(BridgeContext ctx) {
        bridgeContext = ctx;
        document = ctx.getDocument();
        docPURL = new ParsedURL(((SVGDocument)document).getURL());
        userAgent = bridgeContext.getUserAgent();
    }

    /**
     * Creates a new Window object.
     */
    public org.apache.batik.script.Window createWindow
        (Interpreter interp, String lang) {
        return new Window(interp, lang);
    }

    /**
     * Creates a new Window object.
     */
    public org.apache.batik.script.Window createWindow() {
        return createWindow(null, null);
    }

    /**
     * Initializes the environment of the given interpreter.
     */
    public void initializeEnvironment(Interpreter interp, String lang) {
        org.apache.batik.script.Window window = createWindow(interp, lang);
        interp.bindObject("window", window);
        registerWindowObject(window);
    }

    /**
     * Registers a newly created Window object with the document.
     */
    protected void registerWindowObject(org.apache.batik.script.Window window) {
    }

    /**
     * Returns the default Interpreter for this document.
     */
    public Interpreter getInterpreter() {
        if (interpreter != null)
            return interpreter;

        SVGSVGElement root = (SVGSVGElement)document.getDocumentElement();
        String lang = root.getContentScriptType();
        return getInterpreter(lang);
    }

    public Interpreter getInterpreter(String lang) {
        interpreter = bridgeContext.getInterpreter(lang);
        if (interpreter == null) {
            if (languages.contains(lang)) {
                // Already issued warning so just return null;
                return null;
            }

            // So we know we have processed this interpreter.
            languages.add(lang);
            return null;
        }

        if (!languages.contains(lang)) {
            languages.add(lang);
            initializeEnvironment(interpreter, lang);
        }
        return interpreter;
    }

    /**
     * Loads the scripts contained in the <script> elements.
     */
    public void loadScripts() {
        NodeList scripts = document.getElementsByTagNameNS
            (SVGConstants.SVG_NAMESPACE_URI, SVGConstants.SVG_SCRIPT_TAG);
        int len = scripts.getLength();

        if (len == 0) {
            return;
        }

        AbstractElement[] elements      = new AbstractElement[len];
        String[]          types         = new String[len];
        boolean[]         typesJava     = new boolean[len];
        String[]          hrefs         = new String[len];
        ParsedURL[]       scriptPURLs   = new ParsedURL[len];
        Vector            jarURLs       = new Vector();
        HashMap           handlerNames1 = new HashMap();
        HashMap           handlerNames2 = new HashMap();

        // Collect details about the script elements.
        for (int i = 0; i < len; i++) {
            try {
                elements[i] = (AbstractElement) scripts.item(i);
                types[i] = elements[i].getAttributeNS
                    (null, SVGConstants.SVG_TYPE_ATTRIBUTE);

                if (types[i].length() == 0) {
                    types[i] = SVGConstants.SVG_SCRIPT_TYPE_DEFAULT_VALUE;
                }

                typesJava[i] =
                    types[i].equals(SVGConstants.SVG_SCRIPT_TYPE_JAVA);
                hrefs[i] = XLinkSupport.getXLinkHref(elements[i]);
                scriptPURLs[i] =
                    new ParsedURL(elements[i].getBaseURI(), hrefs[i]);
                checkCompatibleScriptURL(types[i], scriptPURLs[i]);
                if (typesJava[i]) {
                    jarURLs.add(new URL(scriptPURLs[i].toString()));
                }
            } catch (Exception e) {
                if (userAgent != null) {
                    userAgent.displayError(e);
                }
                elements[i] = null;
            }
        }

        DocumentJarClassLoader cll = null;
        URL docURL = null;
        if (!jarURLs.isEmpty()) {
            try {
                docURL = new URL(docPURL.toString());
            } catch (MalformedURLException mue) {
                /* nothing just let docURL be null */
            }
            // Make a class loader that lets all jar files see each other.
            cll = new DocumentJarClassLoader
                ((URL[]) jarURLs.toArray(new URL[0]), docURL);

            // Get the 'Script-Handler' and 'SVG-Handler-Class' attributes
            // from the jar files.
            try {
                Enumeration e = cll.findResources("META-INF/MANIFEST.MF");
                while (e.hasMoreElements()) {
                    URL url = (URL) e.nextElement();
                    String fullURL = url.toString();
                    String jarURL = fullURL.substring(4, fullURL.indexOf('!'));
                    Manifest man = new Manifest(url.openStream());
                    String handler =
                        man.getMainAttributes().getValue("Script-Handler");
                    if (handler != null) {
                        handlerNames1.put(jarURL, handler);
                    }
                    handler =
                        man.getMainAttributes().getValue("SVG-Handler-Class");
                    if (handler != null) {
                        handlerNames2.put(jarURL, handler);
                    }
                }
            } catch (Exception ex) {
                if (userAgent != null) {
                    userAgent.displayError(ex);
                }
            }
        }

        // Create a window object.
        org.apache.batik.script.Window window = createWindow();
        registerWindowObject(window);

        // Execute each of the scripts.
        for (int i = 0; i < len; i++) {
            if (elements[i] == null) {
                continue;
            }

            if (typesJava[i]) {
                //
                // Java code invocation.
                //
                try {
                    // Run the script handler specified by 'Script-Handler'.
                    String jarURL = scriptPURLs[i].toString();
                    String handler = (String) handlerNames1.get(jarURL);
                    if (handler != null) {
                        ScriptHandler h;
                        h = (ScriptHandler)cll.loadClass(handler).newInstance();
                        h.run(document, window);
                    }

                    // Run the script handler specified by 'SVG-Handler-Class'.
                    handler = (String) handlerNames2.get(jarURL);
                    if (handler != null) {
                        EventListenerInitializer initializer;
                        initializer = (EventListenerInitializer)
                            cll.loadClass(handler).newInstance();

                        initializer.initializeEventListeners
                            ((SVGDocument)document);
                    }
                } catch (Exception e) {
                    if (userAgent != null) {
                        userAgent.displayError(e);
                    }
                }
            } else {
                //
                // Scripting language invocation.
                //
                Interpreter interpreter = getInterpreter(types[i]);
                if (interpreter == null) {
                    // Can't find interpreter so just skip this script block.
                    continue;
                }

                try {
                    String desc = null;
                    Reader reader;

                    if (hrefs[i].length() > 0) {
                        desc = hrefs[i];

                        // External script.
                        reader = new InputStreamReader
                            (scriptPURLs[i].openStream());
                    } else {
                        DocumentLoader dl = bridgeContext.getDocumentLoader();
                        SVGDocument d =
                            (SVGDocument) elements[i].getOwnerDocument();
                        int line = dl.getLineNumber(elements[i]);
                        desc = Messages.formatMessage
                            (INLINE_SCRIPT_DESCRIPTION,
                             new Object [] {d.getURL(),
                                            "<"+elements[i].getNodeName()+">", 
                                            new Integer(line)});
                        // Inline script.
                        Node n = elements[i].getFirstChild();
                        if (n != null) {
                            StringBuffer sb = new StringBuffer();
                            while (n != null) {
                                if (n.getNodeType() == Node.CDATA_SECTION_NODE
                                    || n.getNodeType() == Node.TEXT_NODE)
                                    sb.append(n.getNodeValue());
                                n = n.getNextSibling();
                            }
                            reader = new StringReader(sb.toString());
                        } else {
                            continue;
                        }
                    }

                    interpreter.evaluate(reader, desc);

                } catch (IOException e) {
                    if (userAgent != null) {
                        userAgent.displayError(e);
                    }
                    return;
                } catch (InterpreterException e) {
                    System.err.println("InterpExcept: " + e);
                    handleInterpreterException(e);
                    return;
                } catch (SecurityException e) {
                    if (userAgent != null) {
                        userAgent.displayError(e);
                    }
                }
            }
        }
    }

    /**
     * Checks that the script URLs and the document url are
     * compatible. A SecurityException is thrown if loading
     * the script is not allowed.
     */
    protected void checkCompatibleScriptURL(String scriptType,
                                          ParsedURL scriptPURL){
        userAgent.checkLoadScript(scriptType, scriptPURL, docPURL);
    }

    /**
     * Recursively dispatch the SVG 'onload' event.
     */
    public void dispatchSVGLoadEvent() {
        SVGSVGElement root = (SVGSVGElement)document.getDocumentElement();
        String lang = root.getContentScriptType();
        dispatchSVGLoad(root, true, lang);
    }

    /**
     * Auxiliary method for dispatchSVGLoad.
     */
    protected void dispatchSVGLoad(Element elt,
                                   boolean checkCanRun,
                                   String lang) {
        for (Node n = elt.getFirstChild();
             n != null;
             n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                dispatchSVGLoad((Element)n, checkCanRun, lang);
            }
        }

        DocumentEvent de = (DocumentEvent)elt.getOwnerDocument();
        AbstractEvent ev = (AbstractEvent) de.createEvent("SVGEvents");
        String type;
        if (bridgeContext.isSVG12()) {
            type = "load";
        } else {
            type = "SVGLoad";
        }
        ev.initEventNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
                       type,
                       false,
                       false);
        NodeEventTarget t = (NodeEventTarget)elt;

        final String s =
            elt.getAttributeNS(null, SVGConstants.SVG_ONLOAD_ATTRIBUTE);
        if (s.length() == 0) {
            // No script to run so just dispatch the event to DOM
            // (For java presumably).
            t.dispatchEvent(ev);
            return;
        }

        final Interpreter interp = getInterpreter();
        if (interp == null) {
            // Can't load interpreter so just dispatch normal event
            // to the DOM (for java presumably).
            t.dispatchEvent(ev);
            return;
        }

        if (checkCanRun) {
            // Check that it is ok to run embeded scripts
            checkCompatibleScriptURL(lang, docPURL);
            checkCanRun = false; // we only check once for onload handlers
        }

        DocumentLoader dl = bridgeContext.getDocumentLoader();
        SVGDocument d = (SVGDocument)elt.getOwnerDocument();
        int line = dl.getLineNumber(elt);
        final String desc = Messages.formatMessage
            (EVENT_SCRIPT_DESCRIPTION,
             new Object [] {d.getURL(),
                            SVGConstants.SVG_ONLOAD_ATTRIBUTE, 
                            new Integer(line)});

        EventListener l = new EventListener() {
                public void handleEvent(Event evt) {
                    try {
                        Object event;
                        if (evt instanceof ScriptEventWrapper) {
                            event = ((ScriptEventWrapper) evt).getEventObject();
                        } else {
                            event = evt;
                        }
                        interp.bindObject(EVENT_NAME, event);
                        interp.bindObject(ALTERNATE_EVENT_NAME, event);
                        interp.evaluate(new StringReader(s), desc);
                    } catch (IOException io) {
                    } catch (InterpreterException e) {
                        handleInterpreterException(e);
                    }
                }
            };
        t.addEventListenerNS
            (XMLConstants.XML_EVENTS_NAMESPACE_URI, type,
             l, false, null);
        t.dispatchEvent(ev);
        t.removeEventListenerNS
            (XMLConstants.XML_EVENTS_NAMESPACE_URI, type,
             l, false);
    }

    /**
     * Method to dispatch SVG Zoom event.
     */
    protected void dispatchSVGZoomEvent() {
        if (bridgeContext.isSVG12()) {
            dispatchSVGDocEvent("zoom");
        } else {
            dispatchSVGDocEvent("SVGZoom");
        }
    }

    /**
     * Method to dispatch SVG Scroll event.
     */
    protected void dispatchSVGScrollEvent() {
        if (bridgeContext.isSVG12()) {
            dispatchSVGDocEvent("scroll");
        } else {
            dispatchSVGDocEvent("SVGScroll");
        }
    }

    /**
     * Method to dispatch SVG Resize event.
     */
    protected void dispatchSVGResizeEvent() {
        if (bridgeContext.isSVG12()) {
            dispatchSVGDocEvent("resize");
        } else {
            dispatchSVGDocEvent("SVGResize");
        }
    }

    protected void dispatchSVGDocEvent(String eventType) {
        SVGSVGElement root =
            (SVGSVGElement)document.getDocumentElement();
        // Event is dispatched on outermost SVG element.
        EventTarget t = root;

        DocumentEvent de = (DocumentEvent)document;
        AbstractEvent ev = (AbstractEvent) de.createEvent("SVGEvents");
        ev.initEventNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
                       eventType,
                       false,
                       false);
        t.dispatchEvent(ev);
    }

    /**
     * Handles the given exception.
     */
    protected void handleInterpreterException(InterpreterException ie) {
        if (userAgent != null) {
            Exception ex = ie.getException();
            userAgent.displayError((ex == null) ? ie : ex);
        }
    }

    /**
     * Handles the given exception.
     */
    protected void handleSecurityException(SecurityException se) {
        if (userAgent != null) {
            userAgent.displayError(se);
        }
    }

    /**
     * Represents the window object of this environment.
     */
    protected class Window implements org.apache.batik.script.Window {

        /**
         * The associated interpreter.
         */
        protected Interpreter interpreter;

        /**
         * The associated language.
         */
        protected String language;

        /**
         * Creates a new Window.
         */
        public Window(Interpreter interp, String lang) {
            interpreter = interp;
            language = lang;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setInterval(String,long)}.
         */
        public Object setInterval(final String script, long interval) {
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setInterval(Runnable,long)}.
         */
        public Object setInterval(final Runnable r, long interval) {
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#clearInterval(Object)}.
         */
        public void clearInterval(Object interval) {
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setTimeout(String,long)}.
         */
        public Object setTimeout(final String script, long timeout) {
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#setTimeout(Runnable,long)}.
         */
        public Object setTimeout(final Runnable r, long timeout) {
            return null;
        }

        /**
         * Implements {@link
         * org.apache.batik.script.Window#clearTimeout(Object)}.
         */
        public void clearTimeout(Object timeout) {
        }

        /**
         * Parses the given XML string into a DocumentFragment of the
         * given document or a new document if 'doc' is null.
         * The implementation in this class always returns 'null'
         * @return The document/document fragment or null on error.
         */
        public Node parseXML(String text, Document doc) {
            return null;
        }

        /**
         * Gets data from the given URI.
         * @param uri The URI where the data is located.
         * @param h A handler called when the data is available.
         */
        public void getURL(String uri, org.apache.batik.script.Window.URLResponseHandler h) {
            getURL(uri, h, "UTF8");
        }

        /**
         * Gets data from the given URI.
         * @param uri The URI where the data is located.
         * @param h A handler called when the data is available.
         * @param enc The character encoding of the data.
         */
        public void getURL(String uri,
                           org.apache.batik.script.Window.URLResponseHandler h,
                           String enc) {
        }

        public void postURL(String uri, String content, 
                            org.apache.batik.script.Window.URLResponseHandler h) {
            postURL(uri, content, h, "text/plain", null);
        }

        public void postURL(String uri, String content, 
                            org.apache.batik.script.Window.URLResponseHandler h, 
                     String mimeType) {
            postURL(uri, content, h, mimeType, null);
        }

        public void postURL(String uri, 
                            String content, 
                            org.apache.batik.script.Window.URLResponseHandler h, 
                            String mimeType, 
                            String fEnc) { 
        }



        /**
         * Displays an alert dialog box.
         */
        public void alert(String message) {
        }

        /**
         * Displays a confirm dialog box.
         */
        public boolean confirm(String message) {
            return false;
        }

        /**
         * Displays an input dialog box.
         */
        public String prompt(String message) {
            return null;
        }

        /**
         * Displays an input dialog box, given the default value.
         */
        public String prompt(String message, String defVal) {
            return null;
        }

        /**
         * Returns the current BridgeContext.
         */
        public BridgeContext getBridgeContext() {
            return bridgeContext;
        }

        /**
         * Returns the associated interpreter.
         */
        public Interpreter getInterpreter() {
            return interpreter;
        }

    }
}
