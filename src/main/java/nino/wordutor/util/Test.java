package nino.wordutor.util;

public class Test {

    public static void main(String[] args) {
        System.out.println(Inflector.getInstance().pluralize("box"));
        System.out.println(Inflector.getInstance().pluralize("woman"));
        System.out.println(Inflector.getInstance().pluralize("child"));
        System.out.println(Inflector.getInstance().pluralize("wall"));
        System.out.println(Inflector.getInstance().pluralize("fly"));
        System.out.println(Inflector.getInstance().pluralize("knife"));
        System.out.println(Inflector.getInstance().singularize("boxes"));
        System.out.println(Inflector.getInstance().pastTenslize("carry"));
        System.out.println(Inflector.getInstance().pastTenslize("fly"));
        System.out.println(Inflector.getInstance().pastTenslize("look"));
        System.out.println(Inflector.getInstance().pastTenslize("help"));
        System.out.println(Inflector.getInstance().pastTenslize("close"));
        System.out.println(Inflector.getInstance().pastTenslize("die"));
        System.out.println(Inflector.getInstance().pastTenslize("like"));
        System.out.println(Inflector.getInstance().present("ooze"));
        System.out.println(Inflector.getInstance().present("climb"));
        System.out.println(Inflector.getInstance().present("lie"));
        System.out.println(Inflector.getInstance().present("picnic"));
    }
}
