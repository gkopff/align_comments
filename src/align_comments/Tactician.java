// ===================================================================
// Tactical comment aligner plugin.
// (c) Copyright 2011 Gregory Kopff
// All Rights Reserved.
// ===================================================================

package align_comments;

import java.io.*;

/**
 *  The tactician class provides the static <code>transform()</code> methods which
 *  take source code, parses it to find the best tactical comment column, and then
 *  re-writes the source to align the tactical comments at that position. <p>
 *  
 *  The tactician can also be invoked as a Java application.  It reads from stdin
 *  and writes to stdout.
 */
public class Tactician
{
  /** The default minimum column position (column 70 using 1-based numbering). */
  private static int DEFAULT_MIN_COLUMN = 69;
  
  /** Line comment identifier. */
  private static final String LINE_COMMENT = "//";
  
  /**
   *  Transform the given source code by aligning the tactical comments.
   *  @param source The source code.
   *  @return Re-formatted source code.
   */
  public static String transform(String source)
  {
    return transform(source, DEFAULT_MIN_COLUMN);
  }
  
  /**
   *  Transform the given source code by aligning the tactical comments.
   *  @param source The source code.
   *  @param minColumn The minimum column index to use (0-based).
   *  @return Re-formatted source code.
   */
  public static String transform(String source, int minColumn)
  {
    final String[] lines;                                                      // source code line by line
    final int column;                                                          // the optimal comment column
    
    lines = source.split("\n");                                                // break the source down into lines
    column = calculateOptimalColumn(lines, minColumn);                         // calculate the optimal comment column
    
    return editLines(lines, column);                                           // perform edits to all lines
  }
  
  /**
   *  Edit the given lines, placing any tactical comment at the given column index.
   *  @param lines The line to edit.
   *  @param column Where the tactical comment should start.
   *  @return A single string containing the whole source (with newline characters).
   */
  private static final String editLines(String[] lines, int column)
  {
    StringBuilder buff = new StringBuilder();                                  // staging buffer
    
    for (String line : lines)                                                  // consider each line ...
    {
      buff.append(editLine(line, column));                                     // ... perform any required edits ...
      buff.append("\n");                                                       // ... and append a newline character
    }
    
    return buff.toString();                                                    // return the result
  }
  
  /**
   *  Edit the given line, placing any tactical comment at the given column index.
   *  @param line The line to edit.
   *  @param column Where the tactical comment should start.
   *  @return The edited line.
   */
  private static final String editLine(String line, int column)
  {
    int commentIndex;                                                          // comment index
    
    commentIndex = locateLineComment(line);                                    // attempt to locate a line comment
    if (commentIndex != -1)                                                    // we found one ...
    {
      final String code;
      final String comment;
      
      code = removeTrailingWhitespace(line.substring(0, commentIndex));        // isolate the code fragment ...
      comment = line.substring(commentIndex);                                  // ... and the comment fragment
      
      if (isTacticallyCommentedCode(code))                                     // is this a tactical comment?
      {
        return align(code, comment, column);                                   // yes ... perform the alignment
      }
      else                                                                     // there were no tactical comments on this line ...
      {
        return line;                                                           // ... so return it unchanged
      }
    }
    else                                                                       // there were no tactical comments on this line ...
    {
      return line;                                                             // ... so return it unchanged
    }
  }
  
  /**
   *  Remove any trailing whitespace from the given string.  (Leading whitespace is not removed).
   *  @param str The string.
   *  @return A string with trailing whitespace removed.
   */
  private static final String removeTrailingWhitespace(String str)
  {
    for (int i = str.length() - 1; i >= 0; i--)                                // work back from the end of the string ...
    {
      if (! Character.isWhitespace(str.charAt(i)))                             // ... until we find a non-white space character ...
      {
        return str.substring(0, i + 1);                                        // ... and chop the string here
      }
    }
    
    return str;                                                                // we didn't find non-whitespace, use the unchanged string
  }
  
  /**
   *  Take the supplied code and comment and place the comment at the given column.
   *  @param code The code fragment.
   *  @param comment The comment fragment.
   *  @param column The column at which to place the comment.
   *  @return The appropriately commented line of code.
   */
  private static final String align(String code, String comment, int column)
  {
    final StringBuilder buff = new StringBuilder();                            // staging buffer
    final int pad = column - code.length();                                    // determine how much padding is required
    
    buff.append(code);                                                         // start with the code
    
    for (int i = 0; i < pad; i++)                                              // add padding as required
    {
      buff.append(" ");
    }
    
    buff.append(comment);                                                      // then add the comment
    
    return buff.toString();                                                    // return the result
  }
  
  /**
   *  Is the comment for this code fragment a tactical comment?
   *  @param code The code fragment.
   *  @return True if we consider the comment to be a tactical comment, false otherwise.
   */
  private static final boolean isTacticallyCommentedCode(String code)
  {
    // We check to see if the string we've isolated as a 'code' block really does contain
    // some code.  If it's just whitespace, then the comment is a strategic comment rather
    // than a tactical comment.
    
    for (int i = 0; i < code.length(); i++)                                    // scan the code looking for non-whitespace
    {
      if (! Character.isWhitespace(code.charAt(i)))                            // found evidence of code ...
      {
        return true;                                                           // so it's a tactical comment
      }
    }
    
    return false;                                                              // it's a strategic comment (or a commented out code block)
  }
  
  /**
   *  Determine the optimal comment column.
   *  @param lines The source code, broken up by line.
   *  @param minColumn The minimum column index to use (0-based).
   *  @return The column index.
   */
  private static final int calculateOptimalColumn(String[] lines, int minColumn)
  {
    int parsed = minColumn;                                                    // prime with the supplied minimum
    
    for (String line : lines)                                                  // consider each source code line ...
    {
      parsed = Math.max(parsed, findEndOfCode(line) + 2);                      // ... finding the longest line (then next column, plus a space)
    }
    
    int baseOne;                                                               // 1-based column index (for clarity)
    int alignment;                                                             // (mis)alignment
    int shift;                                                                 // required shift to align comment
    
    baseOne = parsed + 1;                                                      // convert from 0-based to 1-based for clarity
    alignment = baseOne % 10;                                                  // determine the (mis)alignment on a '10 column' boundary
    shift = alignment != 0 ? 10 - alignment : 0;                               // determine any boundary alignment shift
    
//    System.out.println("Tacticial.calculatOptimalColumn(): minColumn: " + minColumn + " (" + (minColumn+1) + ")");
//    System.out.println("Tacticial.calculatOptimalColumn(): parsed: " + parsed);
//    System.out.println("Tacticial.calculatOptimalColumn(): base 1: " + baseOne);
//    System.out.println("Tacticial.calculatOptimalColumn(): alignment: " + alignment);
//    System.out.println("Tacticial.calculatOptimalColumn(): shift: " + shift);
//    System.out.println("Tacticial.calculatOptimalColumn(): optimal: " + (parsed + shift));
    
    return parsed + shift;                                                     // return the column index
  }
  
  /**
   *  Find the last character of the line of code, after which is only whitespace and line comments.
   *  If there is no tactical comment on this line, we return 0 so that we don't affect the alignment
   *  determination.
   *  @param line The line of source code.
   *  @return The index of column containing the last character of the code fragment.
   */
  private static final int findEndOfCode(String line)
  {
    int commentIndex;                                                          // comment marker index
    
    commentIndex = locateLineComment(line);                                    // attempt to locate a line comment
    if (commentIndex != -1)                                                    // found one
    {
      final String stripped = line.substring(0, commentIndex);                 // remove the comment
      
      for (int i = stripped.length() - 1; i >= 0; i--)                         // work from the end of the stripped string ...
      {
        if (! Character.isWhitespace(stripped.charAt(i)))                      // ... until we find a non-whitespace character ...
        {
          return i;                                                            // ... which is the end of the code
        }
      }
    }
    
    return 0;                                                                  // if there's no comment, we aren't interested in the length
  }
  
  /**
   *  Locate the start of any line comment on this line.
   *  @param line The line of code.
   *  @return The index of the start of the line comment, or -1 if no line comment
   *          was found.
   */
  private static final int locateLineComment(String line)
  {
    return line.indexOf(LINE_COMMENT);                                         // attempt to locate a line comment
  }
  
  /**
   *  Main method.
   *  @param args Command line arguments: none.
   */
  public static void main(String[] args) throws IOException
  {
    final StringBuilder buff;                                                  // staging buffer
    final BufferedReader br;                                                   // input reader
    String line;                                                               // a single line
    
    buff = new StringBuilder();                                                // create the staging buffer
    br = new BufferedReader(new InputStreamReader(System.in));                 // read from stdin
    
    while ((line = br.readLine()) != null)                                     // suck stdin into the buffer
    {
      buff.append(line).append("\n");
    }
    
    System.out.print(Tactician.transform(buff.toString()));                    // write the transformed file to stdout
  }
}
