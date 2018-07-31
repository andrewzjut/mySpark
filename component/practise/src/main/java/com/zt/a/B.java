package com.zt.a;

public class B extends A {
    @Override
    protected void _bb() {
        super._bb();
    }

    @Override
    public void _cc() {
        super._cc();
    }

    public static void main(String[] args) {
        B b = new B();
    }
}
