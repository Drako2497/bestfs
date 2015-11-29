package bestfs;

import java.util.ArrayList;


/**
 * @author Toan Tony Nguyen and Gordon Ngo
 * 
 * A simple filesystem.
 * 
 * Team 11: Toan Tony Nguyen and Gordon Ngo
 * Class: CS149-03
 * Homework Assignment 04 due November 29, 2015
 * 
 * github: https://github.com/toantonyh/bestfs
 * 
 */
public class BestFS {
	Block[] diskspace;
	int[] listOfFreeSpace;
	int indexOfLastFreeSpace;
	int fileID = 0;
	File root;
	ArrayList<File> currentDir = new ArrayList<File>();
	
	
	/**
	 * Prints the current directory.
	 */
	private void currentd() {
		String res = "";
		for(int i = 0; i < currentDir.size(); i++) {
			res += currentDir.get(i).name + "/";
		}
		System.out.println(res);
	}
	
	/**
	 * Creates a new directory in the current directory.
	 * 
	 * @param dname the directory name
	 */
	private void maked(String dname) {
		File newDir = new File(true, fileID, dname, "tony", "rw", 
				currentDir.get(currentDir.size()-1));
		updateParentDir(newDir);
	}
	
	/**
	 * Creates a new file in the current directory.
	 * Allocates diskspace and blocks for the file.
	 * 
	 * @param file the file name
	 */
	private void createf(String file) {
		File newFile = new File(false, fileID, file, "word", "tony", 1, 
				1000, "r", currentDir.get(currentDir.size()-1));
		
		if(isFreeSpace(newFile.bytesSize)) {
			fillFreeSpace(newFile.bytesSize);
			fillBlocks(newFile.bytesSize, newFile.fileID, newFile);
			updateParentDir(newFile);
		} else {
			System.out.println("ERROR: No more free space on disk.");
		}
	}
	
	/**
	 * Extends the file size of an existing file.
	 * File can be located anywhere on disk.
	 * Updates number of blocks used and list of free space.
	 * 
	 * @param file the file name
	 * @param size the size extended
	 */
	private void extendf(String file, int size) {
		File foundFile = findFile(file);
		if(foundFile != null && isFreeSpace(size)) {
			fillFreeSpace(size);
			fillBlocks(size, foundFile.fileID, foundFile);
			foundFile.bytesSize += size;
		}
	}
	
	/**
	 * Truncates the file size of an existing file.
	 * File can be located anywhere on disk.
	 * Updates number of blocks used and list of free space.
	 * 
	 * @param file the file name
	 * @param size the size truncated
	 */
	private void truncf(String file, int size) {
		File foundFile = findFile(file);
		if(foundFile != null && size <= foundFile.bytesSize) {
			truncFreeSpace(size);
			truncBlocks(size, foundFile.fileID, foundFile);
			foundFile.bytesSize -= size;
		}
	}
	
	/**
	 * Clears all data on proxy disk and 
	 * reinitializes root folder and current directory.
	 */
	private void formatd() {
		diskspace = new Block[250];
		int diskSize = diskspace.length;
		for(int i = 0; i < diskSize; i++) {
			diskspace[i] = new Block();
			int blockSize = diskspace[i].blockData.length;
			for(int j = 0; j < blockSize; j++) {
				diskspace[i].blockData[j] = -1;
			}
		}
		
		listOfFreeSpace = new int[1000000];
		for(int i = 0; i < listOfFreeSpace.length; i++) {
			listOfFreeSpace[i] = -1;
		}
		
		indexOfLastFreeSpace = 0;
		fileID = 0;
		currentDir = new ArrayList<File>();
		initCurDir();
	}

	/**
	 * Changes the current directory to an existing child directory.
	 * 
	 * @param dir the directory to go to
	 */
	private void chdir(String dir) {
		File foundFile = findFile(dir);
		ArrayList<File> reverseDir = new ArrayList<File>();
		
		if(foundFile.isFolder) {
			reverseDir.add(foundFile);
			reverseDir = traverseParents(foundFile, reverseDir);

			while(!reverseDir.isEmpty()) {
				currentDir.add(reverseDir.remove(reverseDir.size()-1));
			}
		} else {
			System.out.println("No directory found.");
		}
	}
	
	/**
	 * Initiates the current directory with root.
	 */
	private void initCurDir() {
		root = new File(true, fileID, ".", "tony", "rw", null);
		fileID++;
		currentDir.add(root);
	}
	
	/**
	 * Adds the new children to parent's list of children.
	 * 
	 * @param file the new children file
	 */
	private void updateParentDir(File file) {
		File parent = currentDir.get(currentDir.size()-1);
		parent.childrenDir.add(file);
		fileID++;
	}
	
	/**
	 * Checks if there is enough free space on the disk.
	 * 
	 * @param fileSize the size of the file
	 * @return true - enough space, false - not enough space
	 */
	private boolean isFreeSpace(int fileSize) {
		int availSpace = 0;
		for(int i = 0; i < listOfFreeSpace.length; i++) {
			if(listOfFreeSpace[i] == -1) {
				availSpace++;
			}
		}
		
		if(availSpace >= fileSize) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates list of free space available.
	 * Result is less free space available.
	 * 
	 * @param fileSize the size of the file
	 */
	private void fillFreeSpace(int fileSize) {
		int tmp = indexOfLastFreeSpace;
		for(int i = tmp; i < fileSize; i++) {
			if(listOfFreeSpace[i] == -1) {
				listOfFreeSpace[i] = 1;
				indexOfLastFreeSpace++;
			}
		}
	}
	
	/**
	 * Updates list of free space available.
	 * Result is more free space available.
	 * 
	 * @param fileSize the size of the file
	 */
	private void truncFreeSpace(int fileSize) {
		int tmp = indexOfLastFreeSpace;
		for(int i = tmp; i >= 0; i--) {
			if(listOfFreeSpace[i] == 1) {
				listOfFreeSpace[i] = -1;
				indexOfLastFreeSpace--;
			}
		}
	}
	
	/**
	 * Fills the disk and its block with the file.
	 * 
	 * @param size the size of the file
	 * @param fileID the id of the file
	 * @param file the file
	 */
	private void fillBlocks(int size, int fileID, File file) {
		int fileSize = size;
		int newFileID = fileID;
		
		int diskSize = diskspace.length;
		int blockSize = diskspace[0].blockData.length;
		for(int i = 0; i < diskSize && fileSize != 0; i++) {
			for(int j = 0; j < blockSize && fileSize != 0; j++) {
				if(diskspace[i].blockData[j] == -1) {
					diskspace[i].blockData[j] = newFileID;
					fileSize--;
					
					file.addBlockID(diskspace[i].blockID);
				}
			}
		}
	}
	
	/**
	 * Deletes from the disk and its block the file.
	 * 
	 * @param size the size of the file
	 * @param fileID the id of the file
	 * @param file the file
	 */
	private void truncBlocks(int size, int fileID, File file) {
		int fileSize = size;
		int newFileID = fileID;
		
		int diskSize = diskspace.length;
		int blockSize = diskspace[0].blockData.length;
		for(int i = 0; i < diskSize && fileSize != 0; i++) {
			for(int j = 0; j < blockSize && fileSize != 0; j++) {
				if(diskspace[i].blockData[j] == newFileID) {
					diskspace[i].blockData[j] = -1;
					fileSize--;
					
					if(isBlockEmptyOfFileData(newFileID, diskspace[i].blockData)){
						file.removeBlockID(diskspace[i].blockID);
					}
				}
			}
		}
	}
	
	/**
	 * Checks if a sector block is empty of a file.
	 * 
	 * @param fileID the id of the file
	 * @param blockData the sector block
	 * @return true - sector block does not contain file, false - sector block contains file
	 */
	private boolean isBlockEmptyOfFileData(int fileID, int[] blockData) {
		for(int i = 0; i < blockData.length; i++) {
			if(blockData[i] == fileID) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Finds a file or directory in the disk.
	 * 
	 * @param name the name of the file or directory
	 * @return the file
	 */
	private File findFile(String name) {
		if(name.toLowerCase().equals(root.name)) {
			return root;
		} else if(!root.childrenDir.isEmpty()) {
			return findFileHelper(name, root);
		} else {
			return null;
		}
	}
	
	/**
	 * Recurses through all children of the disk and finds a file or directory
	 * 
	 * @param name the name of the file or directory
	 * @param f the file
	 * @return the file
	 */
	private File findFileHelper(String name, File f) {
		for(File child : f.childrenDir) {
			if(child.name.toLowerCase().equals(name.toLowerCase())){
				return child;
			} 
			if (!child.childrenDir.isEmpty()) {
				findFileHelper(name, child);
			}
		}
		return null;
	}
	
	/**
	 * Traverses up a file or directory's parents to the root.
	 * @param file the starting file
	 * @param listOfParents the list of parent files
	 * @return the list of parent files
	 */
	private ArrayList<File> traverseParents(File file, ArrayList<File> listOfParents) {
		ArrayList<File> tmp = new ArrayList<>();
		if(file.parentDir.fileID == 0) {
			return listOfParents;
		} else if (file.parentDir.fileID != 0) {
			listOfParents.add(file.parentDir);
			traverseParents(file.parentDir, listOfParents);
			return tmp;
		} else {
			return null;
		}
	}
	
	/**
	 * Tests methods
	 */
	private void initialize() {
		formatd();
		currentd();
		maked("testdir");
		createf("testfile");
		extendf("testfile", 100);
		truncf("testfile", 200);
		createf("testfile2");
		extendf("testfile2", 200);
		truncf("testfile2", 400);
		chdir("testdir");
		currentd();
		formatd();
	}
	
	/**
	 * Initializes the file system
	 * 
	 * @param args list of command args
	 */
	public static void main(String[] args) {
		System.out.println("Hello World!");
		BestFS b = new BestFS();
		b.initialize();
	}
}
