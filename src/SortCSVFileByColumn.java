import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;


public class SortCSVFileByColumn {
    static int key;
    static int max_records_in_memory;
    static String line, line_1, line_2;
    static String full_file_name;
    static BufferedReader initial_buffered_reader;
    static File sort_directory;
    static int number_of_initial_files = 0;
    static int record_length = 0;
    static int unique_extension_for_file_name = 0;

    public static void main(String[] args) {
       try {
           CheckProgramParameters(args);

           CreateSortDirectory();

           ExtractInputFileRecordsToInitialFiles();

           SortInitialFiles();

           MergeSortedFiles();

           RenameOutputFile();

       } catch (Exception e) {
           System.out.println("Exception occurred: " + e.getMessage() );
           if(sort_directory != null) sort_directory.delete();
           //e.printStackTrace();
       }
    }

    static void CheckProgramParameters(String[] args) throws IOException {
        try {
            // check args has 3 parameters
            if (args.length != 3)
                throw new IOException("Number of given parameters is " + args.length + ", but program required 3 parameters.");

            // convert Key to column number
            key = Integer.parseInt(args[0]);

            // convert X to int number
            max_records_in_memory = Integer.parseInt(args[1]);

            // check X not smaller then 2
            if (max_records_in_memory < 2) throw new IOException("Given memory range " + max_records_in_memory + " is not valid.");

            // check if file name exists and if read permission valid
            full_file_name = args[2];
            File input_file = new File(full_file_name);

            if (!input_file.exists())
                throw new FileNotFoundException("Failed to find file: '" + full_file_name + "'");
            if (!Files.isReadable(input_file.toPath()))
                throw new FileNotFoundException("Failed to read file: '" + full_file_name + "'");

            // check file record length, this initial the first record data
            CheckFileRecordLength();

            // check if column number - the given key, in range of record length
            if (key < 0 || key >= record_length)
                throw new ArrayIndexOutOfBoundsException("key out of range.");

        }catch(Exception e){
            throw new IOException(e);
        }
    }

    public static void CheckFileRecordLength() throws IOException {
        initial_buffered_reader = new BufferedReader(new FileReader(full_file_name));
        line = initial_buffered_reader.readLine();
        if (line != null && !line.equals("")) {
            String[] current_record_split = line.split(",");
            record_length = current_record_split.length;
        }else{
            throw new IOException("empty input file.");
        }
    }

    public static void CreateSortDirectory() throws IOException {
        String directoryPath = Paths.get(".").toAbsolutePath().normalize().toString() + "\\SortFile";
        sort_directory = new File(directoryPath);
        if (sort_directory.exists() && sort_directory.isDirectory()) throw new FileAlreadyExistsException("Directory already exists: '" + sort_directory.getAbsolutePath() + "'");
        if (!sort_directory.mkdirs())
            throw new IOException("Failed to create directory '" + sort_directory.getAbsolutePath() + "' for an unknown reason.");
    }

    static void ExtractInputFileRecordsToInitialFiles() throws IOException {
        try {
            // iterate on input file create n/max_records_in_memory new files with max_records_in_memory records
            while (line != null) {

                // create new temp file for writing
                number_of_initial_files++;
                File initial_file = new File(sort_directory.getAbsolutePath() + "\\temp_file" + String.valueOf(unique_extension_for_file_name) + ".csv");
                unique_extension_for_file_name++;

                // insert upto max_records_in_memory records to file
                InsertUptoMaxSizeRecordsToInitialFile(initial_file);
            }
            initial_buffered_reader.close();

        } catch (IOException e) {
            //e.printStackTrace();
            throw new IOException(e);
        }
    }

    static void InsertUptoMaxSizeRecordsToInitialFile(File initial_file) throws IOException {
        BufferedWriter buffered_writer = new BufferedWriter(new FileWriter(initial_file.getAbsolutePath()));
        for (int current_number_of_records_in_file = 1; line != null && current_number_of_records_in_file <= max_records_in_memory; current_number_of_records_in_file++) {
            // print line to file
            buffered_writer.write(line);
            line = initial_buffered_reader.readLine();
            if(line != null) buffered_writer.newLine();
        }
        buffered_writer.close();
    }

    static void SortInitialFiles() throws IOException {
        // iterate on every file in directory and send it to be sorted by key column
        File[] directory_listing = sort_directory.listFiles();
        if (directory_listing != null) {
            for (File child : directory_listing) {
                SortSingleFile(child.getAbsolutePath());
            }
        }
    }

    static void SortSingleFile(String file_name) throws IOException {
        try {
            Pair[] file_key_record_pair_array = new Pair[max_records_in_memory];


            // add file records to file_key_record_pair_array array
            AddFileRecordsToPairArray(file_key_record_pair_array, file_name);

            // sort array by pair.first - the selected column to sort by (if 1 record already sorted)
            Arrays.sort(file_key_record_pair_array, new Comparator<Pair>() {
                @Override
                public int compare(Pair p1, Pair p2) {
                    if(p1 != null && p2 != null) return p1.getFirst().compareTo((p2.getFirst()));
                    if(p1 != null) return -1;
                    return 1;

                }
            });

            // write sorted records back to the file
            WriteSortedRecordsToFile(file_key_record_pair_array, file_name);

        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public static void AddFileRecordsToPairArray(Pair[] pair_array, String file_name) throws IOException {
        BufferedReader buffered_reader = new BufferedReader(new FileReader(file_name));
        int i = 0;

        line = buffered_reader.readLine();
        while (line != null) {
            String[] current_record_split = line.split(",");
            pair_array[i] = new Pair(current_record_split[key], line);
            i++;
            line = buffered_reader.readLine();
        }
        buffered_reader.close();
    }

    public static void WriteSortedRecordsToFile(Pair[] pair_array, String file_name) throws IOException {
        BufferedWriter buffered_writer = new BufferedWriter(new FileWriter(file_name));
        for (Pair pair : pair_array) {
            if (pair != null) {
                buffered_writer.write(pair.getSecond());
                buffered_writer.newLine();
            }
        }
        buffered_writer.close();
    }

    public static void MergeSortedFiles() throws IOException {
        while (number_of_initial_files != 1) {
            MergeDirectoryFilesUntilOneRemains();
        }
    }

    static void MergeDirectoryFilesUntilOneRemains() throws IOException {
        File[] directory_listing = sort_directory.listFiles();
        File file_1, file_2;

        // iterate on every 2 files in sort_directory
        if (directory_listing != null){
            number_of_initial_files = directory_listing.length;
            if(directory_listing.length >= 2){
                for (int i = 0 ; i < directory_listing.length ; i++){
                    file_1 = directory_listing[i];
                    if(++i < directory_listing.length){
                        file_2 = directory_listing[i];

                        MergeTwoFiles(file_1,i-1,file_2,i);

                        boolean result_1 = file_1.delete();
                        boolean result_2 = file_2.delete();

                        if(!result_1 || !result_2){
                            throw new IOException("Failed to delete files for an unknown reason.");
                        }
                    }
                }
            }
        }
    }

    static void MergeTwoFiles(File file_1, int index_1, File file_2, int index_2) throws IOException {
        try {
            BufferedReader buffered_reader_1 = new BufferedReader(new FileReader(file_1.getAbsolutePath()));
            BufferedReader buffered_reader_2 = new BufferedReader(new FileReader(file_2.getAbsolutePath()));

            // name new file
            File merged_file = new File(sort_directory.getAbsolutePath() + "\\temp_file" + unique_extension_for_file_name + ".csv");
            unique_extension_for_file_name++;

            //iterate on both files and write smaller value to merged_file
            BufferedWriter buffered_writer = new BufferedWriter(new FileWriter(merged_file.getAbsolutePath()));
            line_1 = buffered_reader_1.readLine();
            line_2 = buffered_reader_2.readLine();

            WriteMergedRecords(buffered_writer, buffered_reader_1, buffered_reader_2);

            // one of the files may ended, check and add remain records
            if(line_1 != null) WriteRemainRecords(buffered_writer, buffered_reader_1, 1);
            if(line_2 != null) WriteRemainRecords(buffered_writer, buffered_reader_2, 2);

            buffered_reader_1.close();
            buffered_reader_2.close();
            buffered_writer.close();


        } catch (IOException e) {
            //e.printStackTrace();
            throw new IOException(e);
        }
    }

    static void WriteMergedRecords(BufferedWriter buffered_writer, BufferedReader file_1_buffered_reader, BufferedReader file_2_buffered_reader) throws IOException {
        while (line_1 != null && line_2 != null) {

            String[] first_record_split = line_1.split(",");
            String[] second_record_split = line_2.split(",");

            if (first_record_split[key].compareTo(second_record_split[key]) > 0) {
                buffered_writer.write(line_2);
                buffered_writer.newLine();
                line_2 = file_2_buffered_reader.readLine();
            } else {
                buffered_writer.write(line_1);
                buffered_writer.newLine();
                line_1 = file_1_buffered_reader.readLine();
            }
        }
    }

    public static void WriteRemainRecords(BufferedWriter buffered_writer, BufferedReader buffered_reader, int current_line_number) throws IOException {
        switch (current_line_number){
            case(1): {
                        while (line_1 != null) {
                            buffered_writer.write(line_1);
                            buffered_writer.newLine();
                            line_1 = buffered_reader.readLine();
                        }
            }
            case(2): {
                        while (line_2 != null) {
                            buffered_writer.write(line_2);
                            buffered_writer.newLine();
                            line_2 = buffered_reader.readLine();
                        }
            }
        }
    }

    public static void RenameOutputFile(){
        File[] directory_listing = sort_directory.listFiles();
        File new_file = new File(sort_directory.getAbsolutePath() + "\\Sorted.csv");

        if(directory_listing != null) directory_listing[0].renameTo(new_file);
    }

}