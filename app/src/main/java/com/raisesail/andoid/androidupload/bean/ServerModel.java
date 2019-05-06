
package com.raisesail.andoid.androidupload.bean;

import java.io.Serializable;

public class ServerModel implements Serializable {
    private static final long serialVersionUID = -828322761336296999L;

    public String method;
    public String ip;
    public String url;
    public String des;
    public String upload;
    public Author author;

    public class Author implements Serializable {
        private static final long serialVersionUID = 2701611773813762723L;

        public String name;
        public String fullname;
        public String github;
        public String address;
        public String qq;
        public String email;
        public String des;

        @Override
        public String toString() {
            return "Author{\n" +//
                   "\tname='" + name + "\'\n" +//
                   "\tfullname='" + fullname + "\'\n" +//
                   "\tgithub='" + github + "\'\n" +//
                   "\taddress='" + address + "\'\n" +//
                   "\tqq='" + qq + "\'\n" +//
                   "\temail='" + email + "\'\n" +//
                   "\tdes='" + des + "\'\n" +//
                   '}';
        }
    }

    @Override
    public String toString() {
        return "ServerModel{\n" +//
               "\tmethod='" + method + "\'\n" +//
               "\tip='" + ip + "\'\n" +//
               "\turl='" + url + "\'\n" +//
               "\tdes='" + des + "\'\n" +//
               "\tupload='" + upload + "\'\n" +//
               "\tauthor=" + author + "\n" +//
               '}';
    }
}
