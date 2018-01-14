package org.openrefine.wikidata.schema;

import java.util.List;

import org.openrefine.wikidata.schema.exceptions.SkipSchemaExpressionException;
import org.openrefine.wikidata.utils.JacksonJsonizable;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class WbItemDocumentExpr extends JacksonJsonizable {

    private WbItemExpr subject;
    private List<WbNameDescExpr> nameDescs;
    private List<WbStatementGroupExpr> statementGroups;
    
    @JsonCreator
    public WbItemDocumentExpr(
            @JsonProperty("subject") WbItemExpr subjectExpr,
            @JsonProperty("nameDescs") List<WbNameDescExpr> nameDescExprs,
            @JsonProperty("statementGroups") List<WbStatementGroupExpr> statementGroupExprs) {
        this.subject = subjectExpr;
        this.nameDescs = nameDescExprs;
        this.statementGroups = statementGroupExprs;
    }
    
    public ItemUpdate evaluate(ExpressionContext ctxt) throws SkipSchemaExpressionException {
        ItemIdValue subjectId = getSubject().evaluate(ctxt);
        ItemUpdate update = new ItemUpdate(subjectId);
        for(WbStatementGroupExpr expr : getStatementGroups()) {
            try {
                for(Statement s : expr.evaluate(ctxt, subjectId).getStatements()) {
                    update.addStatement(s);
                }
            } catch (SkipSchemaExpressionException e) {
                continue;
            }
        }
        for(WbNameDescExpr expr : getNameDescs()) {
            expr.contributeTo(update, ctxt);
        }
        return update;
    }

    @JsonProperty("subject")
    public WbItemExpr getSubject() {
        return subject;
    }

    @JsonProperty("nameDescs")
    public List<WbNameDescExpr> getNameDescs() {
        return nameDescs;
    }

    @JsonProperty("statementGroups")
    public List<WbStatementGroupExpr> getStatementGroups() {
        return statementGroups;
    }
}