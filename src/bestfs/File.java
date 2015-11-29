package bestfs;

import java.util.ArrayList;

/**
 * @author Toan Tony Nguyen and Gordon Ngo
 * 
 * The File class.
 * A file and a directory is treated the same.
 *
 * Team 11: Toan Tony Nguyen and Gordon Ngo
 * Class: CS149-03
 * Homework Assignment 04 due November 29, 2015
 * 
 * github: https://github.com/toantonyh/bestfs
 * 
 */
public class File {
	boolean isFolder;
	int fileID;
	String name;
	String type;
	String owner;
	int numBlocksUsed;
	ArrayList<Integer> blocksUsed;
	int bytesSize;
	String permission;
	File parentDir;
	ArrayList<File> childrenDir;
	
	/**
	 * Constructor for a directory.
	 * 
	 * @param isFolder true if directory, false if file
	 * @param fileID the id of the file
	 * @param name the name of the file
	 * @param owner the owner of the file
	 * @param permission the permission of the file
	 * @param parentDir the parent of the file
	 */
	public File(boolean isFolder, int fileID, String name, String owner,
			String permission, File parentDir) {
		this.isFolder = isFolder;
		this.fileID = fileID;
		this.name = name;
		this.owner = owner;
		this.permission = permission;
		this.parentDir = parentDir;
		childrenDir = new ArrayList<File>();
	}
	
	/**
	 * Constructor for a file.
	 * 
	 * @param isFolder true if directory, false if file
	 * @param fileID the id of the file
	 * @param name the name of the file
	 * @param type the type of the file
	 * @param owner the owner of the file
	 * @param numBlocksUsed the numbers of blocks used
	 * @param bytesSize the size of the file
	 * @param permission the permission of the file
	 * @param parentDir the parent of the file
	 */
	public File(boolean isFolder, int fileID, String name, String type, String owner, 
			int numBlocksUsed, int bytesSize, String permission, File parentDir) {
		this.isFolder = isFolder;
		this.fileID = fileID;
		this.name = name;
		this.type = type;
		this.owner = owner;
		this.numBlocksUsed = numBlocksUsed;
		this.bytesSize = bytesSize;
		this.permission = permission;
		this.parentDir = parentDir;
		this.childrenDir = new ArrayList<File>();
		this.blocksUsed = new ArrayList<Integer>();
	}
	
	/**
	 * Adds the block ID and increases number of blocks used for the file
	 * 
	 * @param blockID the id of the block
	 */
	public void addBlockID(int blockID) {
		if(!this.blocksUsed.contains(blockID)){
			this.blocksUsed.add(blockID);
			this.numBlocksUsed = blocksUsed.size();
		}
	}
	
	/**
	 * Removes the block ID and decreases the number of blocks used for the file
	 * 
	 * @param blockID the id of the block
	 */
	public void removeBlockID(int blockID) {
		if(this.blocksUsed.contains(blockID)){
			this.blocksUsed.remove(blockID);
			this.numBlocksUsed = blocksUsed.size();
		}
	}
}