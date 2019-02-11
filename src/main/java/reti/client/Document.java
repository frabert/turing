package reti.client;

public class Document {
  String name, owner;
  int sections;

  public Document(String name, String owner, int sections) {
    this.name = name.trim();
    this.owner = owner.trim();
    this.sections = sections;
  }

  public String getName() { return name; }
  public String getOwner() { return owner; }
  public int getSections() { return sections; }

  @Override
  public String toString() {
    return name + " (" + owner + ")";
  }
}