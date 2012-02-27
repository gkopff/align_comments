// ===================================================================
// Tactical comment aligner plugin.
// (c) Copyright 2011 Gregory Kopff
// All Rights Reserved.
// ===================================================================

package align_comments.popup.actions;

import org.eclipse.jdt.core.*;
// import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;

import align_comments.*;

public class AlignCommentsAction implements IObjectActionDelegate
{
  /** The current menu selection. */
  private ISelection selection;

  /**
   * Constructor.
   */
  public AlignCommentsAction()
  {
    super();
  }

  /**
   *  @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
   */
  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    ;
  }

  /**
   *  Handle the selection change.
   *  @see IActionDelegate#selectionChanged(IAction, ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  /**
   *  @see IActionDelegate#run(IAction)
   */
  public void run(IAction action)
  {
    if (this.selection instanceof StructuredSelection)                         // provided this is a structed selection ...
    {
      final StructuredSelection ss = (StructuredSelection) this.selection;

      if (ss.getFirstElement() instanceof ICompilationUnit)                    // take the first element ...
      {
        final ICompilationUnit cu = (ICompilationUnit) ss.getFirstElement();   // which is a compilation unit ...
        final IBuffer buff;
        
//        final CompilationUnit comp = parse(cu);
//        final List<LineComment> commentList;
//        commentList = comp.getCommentList();
        
        try
        {
          buff = cu.getBuffer();                                               // extract the source code buffer

          buff.setContents(Tactician.transform(buff.getContents()));           // align the tactical comments
        }
        catch (JavaModelException e)                                           // we don't deal with the model ...
        {
          throw new RuntimeException(e);                                       // ... do we don't expect this to ever happen
        }
      }
    }
    else
    {
      ;
    }
  }
  
//  private CompilationUnit parse(ICompilationUnit unit) 
//  {
//    ASTParser parser = ASTParser.newParser(AST.JLS3); 
//    parser.setKind(ASTParser.K_COMPILATION_UNIT);
//    parser.setSource(unit);
//    parser.setResolveBindings(true);
//    return (CompilationUnit) parser.createAST(null /* IProgressMonitor */);
//  }
}
