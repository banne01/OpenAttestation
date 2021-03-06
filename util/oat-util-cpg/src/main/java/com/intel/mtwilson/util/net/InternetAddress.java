/*
 * Copyright (c) 2013, Intel Corporation. 
 * All rights reserved.
 * 
 * The contents of this file are released under the BSD license, you may not use this file except in compliance with the License.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Intel Corporation nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intel.mtwilson.util.net;

import com.intel.mtwilson.util.validation.Model;
import com.intel.mtwilson.util.validation.ObjectModel;

/**
 * An Internet Address can be a hostname, FQDN, IPv4, or IPv6 address. If any other
 * addressing schemes are used and can be used to route packets or translated
 * to an address that can be used to route packets, they should be added here. 
 * This class is intended
 * as a multi-use container: the operator can use whatever addressing scheme
 * is prevalent in the environment. The same input is returned by toString().
 * 
 * A note about the name:  a network address commonly refers to the first 16
 * bits of an IP address; a subnet address commonly refers to the next 8 bits,
 * and a host address commonly refers to the last 8 bits of an IP address.
 * Oftentimes the terms network address and host address are used loosely to
 * refer to the entire IP address or to a hostname or DNS name.
 * 
 * @author jbuhacoff
 */
public class InternetAddress extends ObjectModel {
    private String input;
    private transient Format format = null;
    private transient Model formatObject = null;
    
    public InternetAddress(String text) {
        input = text.trim();
    }
    
    @Override
    protected void validate() {
        IPv6Address ipv6 = new IPv6Address(input);
        if( ipv6.isValid() ) { 
            format = Format.IPv6; 
            formatObject = ipv6;
            return; 
        }
        
        IPv4Address ipv4 = new IPv4Address(input);
        if( ipv4.isValid() ) { 
            format = Format.IPv4;
            formatObject = ipv4;
            return; 
        }
        
        // XXX TODO: any other format accepted as Hostname? what about DNS / FQDN? There are definitely invalid possibilities for these.
        Hostname hostname = new Hostname(input); // XXX the Hostname validation itself is very weak right now, it accepts anything without commas
        if( hostname.isValid() ) { 
            format = Format.Hostname;
            formatObject = hostname;
            return; 
        }
        
        fault("Unrecognized Internet Address format: %s", input);
    }
    
    @Override
    public String toString() { return input; }
    
    public boolean isIPv6() { return isValid() && format.equals(Format.IPv6); }
    public boolean isIPv4() { return isValid() && format.equals(Format.IPv4); }
    public boolean isHostname() { return isValid() && format.equals(Format.Hostname); }
    
    public Model value() { return isValid() ? formatObject : null; } // returns the underlying model object for the current format (ipv6, ipv4, or hostname) so you can use it without re-validating
    
    public static enum Format { IPv6, IPv4, Hostname; } // TODO: DNS,  FQDN
}
