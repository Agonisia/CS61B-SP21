package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.TreeMap;


public class Head implements Serializable {

    public static File getBranch() {
        String head = Utils.readContentsAsString(Repository.HEAD);
        return new File(head);
    }

    public static String getBranchName() {
        return getBranch().getName();
    }

    public static File getBranchFile(String branchName) {
        return Utils.join(Repository.HEADS_DIR, branchName);
    }

    public static void changeBranch(String headPath) {
        Utils.writeContents(Repository.HEAD, headPath);
    }

    public static Commit getSplitPoint(String masterBranch, String otherBranch) {
        String masterCommitID  = Utils.readContentsAsString(getBranchFile(masterBranch));
        String otherCommitID = Utils.readContentsAsString(getBranchFile(otherBranch));

        TreeMap<String, Integer> masterAncestor = getAncestor(masterCommitID);
        TreeMap<String, Integer> otherAncestor = getAncestor(otherCommitID);

        int minDistance = Integer.MAX_VALUE;
        String lowestCommonAncestorID = null;

        for (String commitID : otherAncestor.keySet()) {
            if (!masterAncestor.containsKey(commitID)) {
                continue;
            }
            int distance = masterAncestor.get(commitID);
            if (distance < minDistance) {
                minDistance = distance;
                lowestCommonAncestorID = commitID;
            }
            if (minDistance == 0) {
                break;
            }
        }

        return Commit.getCommitFromID(lowestCommonAncestorID);
    }

    public static TreeMap<String, Integer> getAncestor(String commitID) {
        TreeMap<String, Integer> ancestor = new TreeMap<>();
        Queue<Commit> commitQueue = new ArrayDeque<>();

        Commit initialCommit = Commit.getCommitFromID(commitID);
        commitQueue.add(initialCommit);
        ancestor.put(commitID, 0);

        while (!commitQueue.isEmpty()) {
            Commit currentCommit = commitQueue.remove();
            int currentDistance = ancestor.get(currentCommit.getCommitID());

            for (Commit parentCommit : currentCommit.getParent()) {
                String parentID = parentCommit.getCommitID();
                if (!ancestor.containsKey(parentID)) {
                    ancestor.put(parentID, currentDistance + 1);
                    commitQueue.add(parentCommit);
                }
            }
        }
        return ancestor;
    }

}
