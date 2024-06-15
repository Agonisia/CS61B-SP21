package gitlet;

// any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date; // You'll likely use this in this class
import java.util.*;

/** Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Pez
 */
public class Commit implements Serializable {
    /**
     * add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private String commitID;
    private Date date;
    private ArrayList<Commit> parent;
    private TreeMap<String, String> blobMapping; // filename and fileSHA1
    DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.CANADA);

    
    /* fill in the rest of this class. */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = new ArrayList<>();
        this.blobMapping = new TreeMap<>();
        this.commitID = idGenerate();
    }

    public Commit(String message, Commit... parentCommit) {
        this.message = message;
        this.date = new Date();
        this.parent = new ArrayList<>(2);
        Collections.addAll(this.parent, parentCommit);
        this.blobMapping = parentCommit[0].getBlobMapping();
        if (parentCommit.length == 2) {
            for (Map.Entry<String, String> entry : parentCommit[1].getBlobMapping().entrySet()) {
                this.blobMapping.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        this.commitID = idGenerate();
    }

    public void save() {
        String head = Utils.readContentsAsString(Repository.HEAD);
        Utils.writeContents(new File(head), this.commitID);
        File commitFile = new File(Repository.COMMIT_DIR, this.commitID);
        Utils.writeObject(commitFile, this);
    }

    private String idGenerate() {
        return Utils.sha1(dateFormat.format(date), message, parent.toString());
    }

    public static Commit getCommitFromID(String commitID) {
        File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
        if (commitFile.exists()) {
            return Utils.readObject(commitFile, Commit.class);
        } else {
            return null;
        }
    }

    public static Commit getCommit() {
        String head = Utils.readContentsAsString(Repository.HEAD);
        String filename = Utils.readContentsAsString(new File(head));
        return Utils.readObject(Utils.join(Repository.COMMIT_DIR, filename), Commit.class);
    }

    public void display() {
        System.out.println("===");
        System.out.println("commit " + this.getCommitID());
        List<Commit> parents = this.getParent();
        if (parents.size() > 1) {
            System.out.println("Merge: " +
                    parents.get(0).getCommitID().substring(0, 7) +
                    " " +
                    parents.get(1).getCommitID().substring(0, 7));
        }
        System.out.println("Date: " + dateFormat.format(date));
        System.out.println(this.getMessage() + "\n");
    }

    public String getMessage() {
        return message;
    }

    public String getCommitID() {
        return commitID;
    }

    public Date getDate() {
        return date;
    }

    public ArrayList<Commit> getParent() {
        return parent;
    }

    public TreeMap<String, String> getBlobMapping() {
        return blobMapping;
    }

    public Set<String> getAllFilename() {
        return blobMapping.keySet();
    }

    public String getBlobSHA1(String filename) {
        return blobMapping.get(filename);
    }

    public Blob getBlob(String filename) {
        String blobSHA1 = this.getBlobSHA1(filename);
        if (blobSHA1 == null) {
            return null;
        }
        return Blob.getBlob(blobSHA1);
    }

    public void addBlobMapping(TreeMap<String, String> somewhereBlobMapping) {
        blobMapping.putAll(somewhereBlobMapping);
    }

    public void removeBlobMapping(TreeMap<String, String> somewhereBlobMapping) {
        for (Map.Entry<String, String> entry : somewhereBlobMapping.entrySet()) {
            blobMapping.remove(entry.getKey());
        }
    }

}
