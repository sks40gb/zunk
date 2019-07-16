package com.core.innerclass;

/**
 *
 * @author sunil
 * This code snippet kind of answers your question already. The class Role is used by the employee interface in
 * getRole() method. The designer of the interface decided that this class is so tightly coupled with the interface
 * that it is worth to define it inside that interface to emphasize how important that class is for the interface.
 */
public interface ClassInsideInterface {

    class Role {

        public String rollname;
        public int roleId;
        public Object person;
        
        public void print(){
            System.out.println("Print the role here.");
        }
    } 

    Role getRole();
    // other methods
}

class Test implements ClassInsideInterface {

    public static void main(String[] args) {
        Test s = new Test();
        Role role = s.getRole();
        role.print();
    }

    public Role getRole() {
        return new Role();
    }
}
