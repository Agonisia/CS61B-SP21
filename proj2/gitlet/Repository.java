package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

// any imports you need here

/** Represents a gitlet repository.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  @author Pez
 */
public class Repository {
    /**
     * add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File INDEX = join(GITLET_DIR, "index");

    /** The object directory. */
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECT_DIR, "blobs");

    /** The refs directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    public static final File REMOTES_DIR = join(REFS_DIR, "remotes");

    // initial branch: master
    public static final File MASTER_DIR = join(HEADS_DIR, "master");


    /* fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println(
                    "A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            GITLET_DIR.mkdir();
            // object
            OBJECT_DIR.mkdir();
            COMMIT_DIR.mkdir();
            BLOBS_DIR.mkdir();
            // refs
            REFS_DIR.mkdir();
            HEADS_DIR.mkdir();
            REMOTES_DIR.mkdir();
        }
        Head.changeBranch(Repository.MASTER_DIR.getPath());
        Commit commit = new Commit();
        commit.save();
        Utils.writeObject(Repository.INDEX, new Stage());
    }

    public static void add(String filename) {
        File file = getFile(filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Commit commit = Commit.getCommit();
        Stage stage = Stage.getStage();
        Blob blob = new Blob(filename);
        blob.save();
        /*
        * If the current working version of the file is identical
        * to the version in the current commit,
        * do not stage it to be added
        * remove it from the staging area if it is already there */
        if (blob.getBlobSHA1().equals(commit.getBlobSHA1(filename))) {
            stage.getAddStage().remove(filename);
        } else {
            stage.addStage(blob);
        }
        if (stage.getRemoveStage().containsValue(blob.getBlobSHA1())) {
            stage.getRemoveStage().remove(filename);
        }
        stage.save();
    }

    public static void commit(String message) {
        Stage stage = Stage.getStage();
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit parentCommit = Commit.getCommit();
        Commit newCommit = new Commit(message, parentCommit);
        newCommit.addBlobMapping(stage.getAddStage());
        newCommit.removeBlobMapping(stage.getRemoveStage());

        stage.clear();
        newCommit.save();
    }

    public static void log() {
        Commit commit = Commit.getCommit();
        while (!commit.getParent().isEmpty()) {
            commit.display();
            commit = commit.getParent().get(0);
        }
        commit.display();
    }

    public static void checkoutBranch(String branchName) {
        // case1
        String currentBranchName = Head.getBranchName();
        if (currentBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        // case2
        File branch = Head.getBranchFile(branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        // case3
        String commitID = Utils.readContentsAsString(branch);
        fileUntrackedCheck(commitID);
        commitRollback(commitID);
        Head.changeBranch(branch.getPath());
        Stage.getStage().clear();
    }

    public static void checkoutFile(String filename) {
        Commit commit = Commit.getCommit();
        Blob blob = commit.getBlob(filename);
        if (blob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File file = Utils.join(filename);
        Utils.writeContents(file, blob.getContent());
    }

    public static void checkoutSpecifiedFile(String commitID, String filename) {
        Commit commit = Commit.getCommitFromID(commitID);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Blob blob = commit.getBlob(filename);
        if (blob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File file = Utils.join(filename);
        Utils.writeContents(file, blob.getContent());
    }

    public static void remove(String filename) {
        Stage stage = Stage.getStage();
        Commit commit = Commit.getCommit();
        if (stage.getAddStageBlobSHA1(filename) == null
                && commit.getBlobSHA1(filename) == null) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        stage.addStageRemove(filename);
        stage.save();
        if (commit.getBlobSHA1(filename) != null) {
            Blob blob = Blob.getBlob(commit.getBlobSHA1(filename));
            stage.removeStage(blob);
            stage.save();
            getFile(filename).delete();
        }
    }

    public static void logGlobal() {
        List<String> allCommitID = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        for (String commitID : allCommitID) {
            Commit commit = Commit.getCommitFromID(commitID);
            commit.display();
        }
    }

    public static void find(String message) {
        boolean flag = false;
        File[] allCommitFile = COMMIT_DIR.listFiles();
        if (allCommitFile != null) {
            for (File commitFile : allCommitFile) {
                Commit commit = Utils.readObject(commitFile, Commit.class);
                if (commit.getMessage().equals(message)) {
                    System.out.println(commit.getCommitID());
                    flag = true;
                }
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        String currentHead = Head.getBranchName();
        List<String> allHead = Utils.plainFilenamesIn(Repository.HEADS_DIR);
        if (allHead != null) {
            for (String head : allHead) {
                if (head.equals(currentHead)) {
                    System.out.print("*");
                }
                System.out.println(head);
            }
        }

        Stage stage = Stage.getStage();
        System.out.println("\n=== Staged Files ===");
        TreeMap<String, String> addStageContent = stage.getAddStage();
        for (String key : addStageContent.keySet()) {
            System.out.println(key);
        }

        System.out.println("\n=== Removed Files ===");
        TreeMap<String, String> removeStageContent = stage.getRemoveStage();
        for (String key : removeStageContent.keySet()) {
            System.out.println(key);
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");
        Commit commit = Commit.getCommit();
        /* modified case 1:
        * File exists in the current commit
        * its version differs from the working directory,
        * not staged in addStage.*/
        for (String filename : commit.getAllFilename()) {
            File file = getFile(filename);
            String commitBlobSHA1 = commit.getBlobSHA1(filename);
            String addStageBlobSHA1 = stage.getAddStageBlobSHA1(filename);
            String removeStageBlobSHA1 = stage.getRemoveStageBlobSHA1(filename);
            if (file.exists()) {
                Blob blob = new Blob(filename);
                String blobSHA1 = blob.getBlobSHA1();
                if (commitBlobSHA1 != null && blobSHA1 != null && !commitBlobSHA1.equals(blobSHA1)
                        && (addStageBlobSHA1 == null || !addStageBlobSHA1.equals(blobSHA1))) {
                    System.out.println(filename + " (modified)");
                }
            } /*else if (removeStageBlobSHA1 == null) {
                //System.out.println(filename + " (deleted)");
                // why?
            }*/
        }
        /* modified case 2:
         * Stored in addStage
         * but does not match the files in the current directory.*/
        /* deleted case 1:
         * Stored in addStage
         * but not in current working directory.*/
        for (String filename : stage.getAddStage().keySet()) {
            File file = getFile(filename);
            String addStageBlobSHA1 = stage.getAddStageBlobSHA1(filename);
            if (file.exists()) {
                Blob blob = new Blob(filename);
                String blobSHA1 = blob.getBlobSHA1();
                if (addStageBlobSHA1 != null && !addStageBlobSHA1.equals(blobSHA1)) {
                    System.out.println(filename + " (modified)");
                }
            } else {
                System.out.println(filename + " (deleted)");
            }
        }

        System.out.println("\n=== Untracked Files ===");
        for (String fileName : getUntrackedFile()) {
            System.out.println(fileName);
        }
    }

    public static void branch(String branchName) {
        File newBranch = Head.getBranchFile(branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Commit commit = Commit.getCommit();
        Utils.writeContents(newBranch, commit.getCommitID());
    }

    public static void branchRemove(String branchName) {
        File branch = Head.getBranchFile(branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (Head.getBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branch.delete();
    }

    public static void reset(String commitID) {
        Commit commit = Commit.getCommitFromID(commitID);
        Stage stage = Stage.getStage();
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        fileUntrackedCheck(commitID);
        commitRollback(commitID);
        Utils.writeContents(Head.getBranch(), commitID);
        stage.clear();
    }

    public static void merge(String mergeBranchName) {
        Stage stage = Stage.getStage();
        if (!stage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        File mergeBranchFile = Head.getBranchFile(mergeBranchName);
        if (!mergeBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        String currentBranchName = Head.getBranchName();
        if (currentBranchName.equals(mergeBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        Commit master = Commit.getCommit();
        Commit other = Commit.getCommitFromID(readContentsAsString(mergeBranchFile));
        Commit split = Head.getSplitPoint(currentBranchName, mergeBranchName);

        fileUntrackedCheck(other.getCommitID());

        if (split.getCommitID().equals(other.getCommitID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
        } else if (split.getCommitID().equals(master.getCommitID())) {
            checkoutBranch(mergeBranchName);
            System.out.println("Current branch fast-forwarded.");
        } else {
            mergePerform(master, other, split, mergeBranchName, currentBranchName);
        }
    }


    // assist function below

    private static void mergePerform(Commit master, Commit other, Commit split,
                                     String mergeBranchName, String currentBranchName) {
        TreeMap<String, Blob> addMapping = new TreeMap<>();
        TreeMap<String, Blob> removeMapping = new TreeMap<>();
        Set<String> allFilename = new TreeSet<>();

        allFilename.addAll(master.getAllFilename());
        allFilename.addAll(other.getAllFilename());
        allFilename.addAll(split.getAllFilename());

        for (String filename : allFilename) {
            String masterBlobSHA1 = master.getBlobSHA1(filename);
            String otherBlobSHA1 = other.getBlobSHA1(filename);
            String splitBlobSHA1 = split.getBlobSHA1(filename);

            if (splitBlobSHA1 == null) {
                if (otherBlobSHA1 != null) {
                    if (masterBlobSHA1 == null) {
                        addMapping.put(filename, Blob.getBlob(otherBlobSHA1));
                    } else {
                        if (!otherBlobSHA1.equals(masterBlobSHA1)) {
                            addMapping.put(filename,
                                    blobMerge(masterBlobSHA1, otherBlobSHA1));
                        }
                    }
                }
            } else {
                if (otherBlobSHA1 == null) {
                    if (masterBlobSHA1 != null) {
                        if (splitBlobSHA1.equals(masterBlobSHA1)) {
                            removeMapping.put(filename, Blob.getBlob(masterBlobSHA1));
                        } else {
                            addMapping.put(filename,
                                    blobMerge(masterBlobSHA1, otherBlobSHA1));
                        }
                    }
                } else {
                    if (masterBlobSHA1 == null) {
                        if (!splitBlobSHA1.equals(otherBlobSHA1)) {
                            addMapping.put(filename,
                                    blobMerge(masterBlobSHA1, otherBlobSHA1));
                        }
                    } else {
                        if (!splitBlobSHA1.equals(otherBlobSHA1)) {
                            if (!splitBlobSHA1.equals(masterBlobSHA1)) {
                                if (!otherBlobSHA1.equals(masterBlobSHA1)) {
                                    addMapping.put(filename,
                                            blobMerge(masterBlobSHA1, otherBlobSHA1));
                                }
                            } else {
                                addMapping.put(filename, Blob.getBlob(otherBlobSHA1));
                            }
                        }
                    }
                }
            }
        }

        Stage stage = Stage.getStage();

        for (Map.Entry<String, Blob> blobMapping : addMapping.entrySet()) {
            Blob blob = blobMapping.getValue();
            blob.save();
            File file = join(blobMapping.getKey());
            writeContents(file, blob.getContent());
            stage.addStage(blobMapping.getKey(), blob.getBlobSHA1());
        }
        for (Map.Entry<String, Blob> blobMapping : removeMapping.entrySet()) {
            Blob blob = blobMapping.getValue();
            getFile(blobMapping.getKey()).delete();
            stage.removeStage(blobMapping.getKey(), blob.getBlobSHA1());
        }

        String message = "Merged " + mergeBranchName + " into " + currentBranchName + ".";
        Commit newCommit = new Commit(message, master, other);
        newCommit.addBlobMapping(master.getBlobMapping());
        newCommit.addBlobMapping(stage.getAddStage());
        newCommit.removeBlobMapping(stage.getRemoveStage());
        stage.clear();
        newCommit.save();
    }

    private static Blob blobMerge(String masterBlobSHA1, String otherBlobSHA1) {
        byte[] conflictStart = "<<<<<<< HEAD\n".getBytes();
        byte[] conflictSeparator = "=======\n".getBytes();
        byte[] conflictEnd = ">>>>>>>\n".getBytes();
        byte[] masterBlobContent = new byte[0];
        byte[] otherBlobContent = new byte[0];

        if (masterBlobSHA1 != null) {
            masterBlobContent = Blob.getBlob(masterBlobSHA1).getContent();
        }
        if (otherBlobSHA1 != null) {
            otherBlobContent = Blob.getBlob(otherBlobSHA1).getContent();
        }

        byte[] resultContent = new byte[conflictStart.length
                + masterBlobContent.length + conflictSeparator.length
                + otherBlobContent.length + conflictEnd.length];
        int len = 0;
        System.arraycopy(conflictStart, 0, resultContent, 0, conflictStart.length);
        len += conflictStart.length;
        System.arraycopy(masterBlobContent, 0, resultContent, len, masterBlobContent.length);
        len += masterBlobContent.length;
        System.arraycopy(conflictSeparator, 0, resultContent, len, conflictSeparator.length);
        len += conflictSeparator.length;
        System.arraycopy(otherBlobContent, 0, resultContent, len, otherBlobContent.length);
        len += otherBlobContent.length;
        System.arraycopy(conflictEnd, 0, resultContent, len, conflictEnd.length);

        System.out.println("Encountered a merge conflict.");
        return new Blob(resultContent);
    }

    private static List<String> getUntrackedFile() {
        List<String> untrackedFile = new ArrayList<>();
        Commit commit = Commit.getCommit();
        Stage stage = Stage.getStage();
        for (String filename : Utils.plainFilenamesIn(Repository.CWD)) {
            File file = getFile(filename);
            if ((commit.getBlobSHA1(filename) == null
                    && stage.getAddStageBlobSHA1(filename) == null)
                    || (file.exists() && stage.getRemoveStageBlobSHA1(filename) != null)) {
                untrackedFile.add(filename);
            }
        }
        return untrackedFile;
    }


    private static void fileUntrackedCheck(String commitID) {
        List<String> untrackedFile = getUntrackedFile();
        Commit commit = Commit.getCommitFromID(commitID);
        for (String filename : untrackedFile) {
            if (commit.getBlobSHA1(filename) != null) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private static void commitRollback(String commitID) {
        Commit commit = Commit.getCommitFromID(commitID);
        for (Map.Entry<String, String> blobMapping : commit.getBlobMapping().entrySet()) {
            Blob blob = Blob.getBlob(blobMapping.getValue());
            File file = Utils.join(blobMapping.getKey());
            Utils.writeContents(file, blob.getContent());
        }

        for (String fileName : Utils.plainFilenamesIn(Repository.CWD)) {
            if (commit.getBlobSHA1(fileName) == null) {
                getFile(fileName).delete();
            }
        }
    }

    private static File getFile(String filename) {
        return new File(CWD, filename);
    }
}
