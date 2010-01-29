package com.redhat.qe.api.helper;
/**
 * Copyright (c) 2009 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */



import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import redstone.xmlrpc.XmlRpcClient;

/**
 * CobblerConnection - represents an authenticatable 
 * XMLRPC connection to Server.
 */

public class XmlRpcClientHelper {

    private XmlRpcClient client;
    private String token;

    /**
     * Constructor creates an authenticated connection to Server.
     * read-only XMLRPC is not supported.
     * @param url  Server XMLRPC endpoint, ex: http://server/api_path
     * @param username Server XMLRPC username
     * @param password Server XMLRPC password
     * @throws XmlRpcException on remote or communication errors
     */
    
    public XmlRpcClientHelper(String loginMethod,String url, String user, String pass) {
        try {
            client = new XmlRpcClient(url, false);
        }
        catch (MalformedURLException e) {
            throw new XmlRpcException(e);
        }
        token = (String) invokeNoTokenMethod(loginMethod, user, pass);
    }    
    
    /**
     * Invoke an XMLRPC method.
     * @param method method to invoke
     * @param args args to pass to method
     * @return Object data returned.
     */
    public Object invokeNoTokenMethod(String method, Object... args) {
        try {
            return client.invoke(method, Arrays.asList(args));
        } 
        catch (Exception e) {
            throw new XmlRpcException("XmlRpcException calling Server.", e);
        } 
    }

    /**
     * Invoke an XMLRPC method.
     * @param method method to invoke
     * @param args args to pass to method
     * @return Object data returned.
     */
    public Object invokeMethod(String method, Object ... args) {
    	List params = new LinkedList(Arrays.asList(args));
    	params.add(token);
    	return invokeNoTokenMethod(method, params.toArray(new Object[0]));
    }    
    

}