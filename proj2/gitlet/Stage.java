package gitlet;

import java.io.Serializable;
import java.util.TreeMap;

public class Stage implements Serializable {
    private TreeMap<String, String> addStage;
    private TreeMap<String, String> removeStage;

    public Stage() {
        addStage = new TreeMap<>();
        removeStage = new TreeMap<>();
    }


    public void addStage(Blob blob) {
        addStage.put(blob.getFilename(), blob.getBlobSHA1());
    }

    public void addStage(String filename, String blobSHA1) {
        addStage.put(filename, blobSHA1);
    }

    public void removeStage(Blob blob) {
        removeStage.put(blob.getFilename(), blob.getBlobSHA1());
    }

    public void removeStage(String filename, String blobSHA1) {
        removeStage.put(filename, blobSHA1);
    }

    public void save() {
        Utils.writeObject(Repository.INDEX, this);
    }

    public void addStageRemove(String filename) {
        addStage.remove(filename);
    }

    public void removeStageRemove(String filename) {
        removeStage.remove(filename);
    }

    public static Stage getStage() {
        return Utils.readObject(Repository.INDEX, Stage.class);
    }

    public TreeMap<String, String> getAddStage() {
        return addStage;
    }

    public String getAddStageBlobSHA1(String filename) {
        return addStage.get(filename);
    }

    public TreeMap<String, String> getRemoveStage() {
        return removeStage;
    }

    public String getRemoveStageBlobSHA1(String filename) {
        return removeStage.get(filename);
    }

    public void clear() {
        addStage.clear();
        removeStage.clear();
        save();
    }

    public boolean isEmpty() {
        return addStage.isEmpty() && removeStage.isEmpty();
    }

}
