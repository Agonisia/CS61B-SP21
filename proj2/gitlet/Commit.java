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
    private TreeMap<String, String> blobMapping; // filename and blobSHA1
    private static final int FULL_COMMIT_ID_LENGTH = 40;

    
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
        return Utils.sha1(this.toString());
    }

    private String getTimestamp() {
        // both
        DateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.CANADA);
        return dateFormat.format(date);
    }

    public static Commit getCommitFromID(String commitID) {
        // case for short id
        if (commitID.length() < FULL_COMMIT_ID_LENGTH) {
            File[] allCommits = Repository.COMMIT_DIR.listFiles();
            if (allCommits != null) {
                File closestMatch = null;
                int closestDistance = Integer.MAX_VALUE;

                for (File file : allCommits) {
                    String filename = file.getName();
                    int distance = getLevenshteinDistance(commitID, filename);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestMatch = file;
                    }
                }

                if (closestMatch != null) {
                    return Utils.readObject(closestMatch, Commit.class);
                }
            }
            return null;
        } else {
            File commitFile = Utils.join(Repository.COMMIT_DIR, commitID);
            if (commitFile.exists()) {
                return Utils.readObject(commitFile, Commit.class);
            } else {
                return null;
            }
        }
    }

    public static Commit getCommit() {
        String head = Utils.readContentsAsString(Repository.HEAD);
        String filename = Utils.readContentsAsString(new File(head));
        return Utils.readObject(Utils.join(Repository.COMMIT_DIR, filename), Commit.class);
    }

    @Override
    public String toString() {
        return "Commit{"
                + "message='" + this.message + '\''
                + ", date=" + this.date
                + ", pathToBlobID=" + this.blobMapping.toString()
                + ", parents=" + this.parent
                + '}';
    }

    public void display() {
        System.out.println("===");
        System.out.println("commit " + this.getCommitID());
        List<Commit> parents = this.getParent();
        if (parents.size() > 1) {
            System.out.println("Merge: "
                    + parents.get(0).getCommitID().substring(0, 7)
                    + " "
                    + parents.get(1).getCommitID().substring(0, 7));
        }
        System.out.println("Date: " + this.getTimestamp());
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

    private static int getLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1]
                                    + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

}
