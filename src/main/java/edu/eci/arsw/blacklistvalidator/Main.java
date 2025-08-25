package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 * Entry point for sequential blacklist validation demonstration.
 *
 * Demonstrates the basic functionality of HostBlackListsValidator using
 * a single-threaded approach with a predefined test IP address.
 *
 * @author hcadavid
 * @version 1.0
 */
public class Main {
    public static void main(String a[]){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> blackListOcurrences=hblv.checkHost("200.24.34.55");
        System.out.println("The host was found in the following blacklists:" + blackListOcurrences);
    }
}
