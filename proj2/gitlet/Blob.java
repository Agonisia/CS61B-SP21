package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;


/** Represents a gitlet blob object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  class blob simply do 2 things:
 *  1. Base on given file, generate the corresponding SHA1 value.
 *     Save blob to .gitlet/objects/bolbs
 *  2. Deserialize the given SHA1 value, get the corresponding blob
 *
 *  @author Pez
 */
public class Blob implements Serializable {

    /** A brief overview
     * */
    private String filename;
    private String blobSHA1;
    private byte[] content;


    public Blob(String filename) {
        this.filename = filename;
        this.content = Utils.readContents(Utils.join(Repository.CWD, this.filename));
        this.blobSHA1 = Utils.sha1(this.content);
    }

    public Blob(byte[] content) {
        this.content = content;
        this.blobSHA1 = Utils.sha1(this.content);
    }

    public void save() {
        File blobDir = join(Repository.BLOBS_DIR, blobSHA1);
        Utils.writeObject(blobDir, this);
    }

    public static Blob getBlob(String blobSHA1) {
        File blobDir = join(Repository.BLOBS_DIR, blobSHA1);
        return Utils.readObject(blobDir, Blob.class);
    }

    // Getters for testing or other purposes if needed
    public String getFilename() {
        return filename;
    }

    public String getBlobSHA1() {
        return blobSHA1;
    }

    public byte[] getContent() {
        return content;
    }

}
