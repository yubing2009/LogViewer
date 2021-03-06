package fr.esrf.logviewer;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.Database;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Dynamic tango tree
 */
abstract class TangoNode extends DefaultMutableTreeNode {

    private boolean areChildrenDefined = false;
    Database db;

    // Create node on the fly and return number of child

    public int getChildCount() {

        try {

            if (!areChildrenDefined) {
                areChildrenDefined = true;
                populateNode();
            }

        } catch (DevFailed e) {

            Main.showTangoError(e);

        }

        return super.getChildCount();

    }

    // Clear all child nodes

    @SuppressWarnings("unused")
    public void clearNodes() {
        removeAllChildren();
        areChildrenDefined = false;
    }

    // Fill children list

    abstract void populateNode() throws DevFailed;

    // Returns true if the node is a leaf, false otherwise

    public boolean isLeaf() {
        return false;
    }

}


// ---------------------------------------------------------------

class RootNode extends TangoNode {

    RootNode(Database db) {
        this.db = db;
    }
    void populateNode() throws DevFailed {
        String[] domains = db.get_device_domain("*");
        for (String domain : domains)
            add(new DomainNode(db, domain));
    }
    public String toString() {
        return "Device: ";
    }
}

// ---------------------------------------------------------------

class DomainNode extends TangoNode {

    String domain;
    DomainNode(Database db, String domain) {
        this.db = db;
        this.domain = domain;
    }
    void populateNode() throws DevFailed {
        String[] families = db.get_device_family(domain + "/*");
        for (String family : families)
            add(new FamilyNode(db, domain, family));
    }
    public String toString() {
        return domain;
    }
}

// ---------------------------------------------------------------

class FamilyNode extends TangoNode {

    String domain;
    String family;
    FamilyNode(Database db, String domain, String family) {
        this.domain = domain;
        this.family = family;
        this.db = db;
    }
    void populateNode() throws DevFailed {
        String prefix = domain + "/" + family + "/";
        String[] members = db.get_device_member(prefix + "*");
        for (String member : members)
            add(new DeviceNode(db, domain, family, member));
    }
    public String toString() {
        return family;
    }
}

// ---------------------------------------------------------------

class DeviceNode extends TangoNode {

    String domain;
    String family;
    String member;
    String devName;

    DeviceNode(Database db, String domain, String family, String member) {
        this.domain = domain;
        this.family = family;
        this.member = member;
        this.db = db;
        devName = this.domain + "/" + this.family + "/" + member;
    }

    void populateNode() throws DevFailed {
    }

    public String toString() {
        return member;
    }

    public boolean isLeaf() {
        return true;
    }

}

// ---------------------------------------------------------------
