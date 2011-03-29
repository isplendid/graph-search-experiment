package sjtu.apex.gse.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Value;
import org.openrdf.query.algebra.And;
import org.openrdf.query.algebra.BNodeGenerator;
import org.openrdf.query.algebra.Bound;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.CompareAll;
import org.openrdf.query.algebra.CompareAny;
import org.openrdf.query.algebra.Count;
import org.openrdf.query.algebra.Datatype;
import org.openrdf.query.algebra.Difference;
import org.openrdf.query.algebra.Distinct;
import org.openrdf.query.algebra.EmptySet;
import org.openrdf.query.algebra.Exists;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.Filter;
import org.openrdf.query.algebra.FunctionCall;
import org.openrdf.query.algebra.Group;
import org.openrdf.query.algebra.GroupElem;
import org.openrdf.query.algebra.In;
import org.openrdf.query.algebra.Intersection;
import org.openrdf.query.algebra.IsBNode;
import org.openrdf.query.algebra.IsLiteral;
import org.openrdf.query.algebra.IsResource;
import org.openrdf.query.algebra.IsURI;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.Label;
import org.openrdf.query.algebra.Lang;
import org.openrdf.query.algebra.LangMatches;
import org.openrdf.query.algebra.LeftJoin;
import org.openrdf.query.algebra.Like;
import org.openrdf.query.algebra.LocalName;
import org.openrdf.query.algebra.MathExpr;
import org.openrdf.query.algebra.Max;
import org.openrdf.query.algebra.Min;
import org.openrdf.query.algebra.MultiProjection;
import org.openrdf.query.algebra.Namespace;
import org.openrdf.query.algebra.Not;
import org.openrdf.query.algebra.Or;
import org.openrdf.query.algebra.Order;
import org.openrdf.query.algebra.OrderElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.QueryModelVisitor;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.algebra.Reduced;
import org.openrdf.query.algebra.Regex;
import org.openrdf.query.algebra.SameTerm;
import org.openrdf.query.algebra.SingletonSet;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Str;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;
import org.openrdf.rio.ntriples.NTriplesUtil;

import sjtu.apex.gse.indexer.IDManager;
import sjtu.apex.gse.struct.QueryGraph;
import sjtu.apex.gse.struct.QueryGraphNode;
import sjtu.apex.gse.struct.QuerySchema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SPARQLConvert implements QueryModelVisitor<Exception> {
	
	private List<QueryGraphNode> selectedVars;
	private IDManager idman;
	private QueryGraph graph;
	Map<String, QueryGraphNode> nameNodeMap;
	
	public SPARQLConvert(IDManager idman) {
		selectedVars = new ArrayList<QueryGraphNode>();
		graph = new QueryGraph();
		nameNodeMap = new HashMap<String, QueryGraphNode>();
		this.idman = idman;
	}
	
	public QuerySchema getQuerySchema() {
		return new QuerySchema(graph, selectedVars);
	}
	
	private QueryGraphNode getMappedNode(Var target) {
		QueryGraphNode qn = null;
		
		if (target.isAnonymous() && target.getValue() != null) {
			Value v = target.getValue();
			
			String sv = NTriplesUtil.toNTriplesString(v);
			qn = graph.addNode(idman.addGetID(sv));
		} else {
			String name = target.getName();
			
			if (nameNodeMap.containsKey(name))
				qn = nameNodeMap.get(name);
			else {
				qn = graph.addNode();
				nameNodeMap.put(name, qn);
			}
		}
		
		return qn;
	}

	@Override
	public void meet(QueryRoot arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(And arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(BNodeGenerator arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Bound arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Compare arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(CompareAll arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(CompareAny arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Count arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Datatype arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Difference arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Distinct arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(EmptySet arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Exists arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Extension arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(ExtensionElem arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(FunctionCall arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Group arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(GroupElem arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(In arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Intersection arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(IsBNode arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(IsLiteral arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(IsResource arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(IsURI arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Join j) throws Exception {
		j.getLeftArg().visit(this);
		j.getRightArg().visit(this);
	}

	@Override
	public void meet(Label arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Lang arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(LangMatches arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Like arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(LocalName arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(MathExpr arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Max arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(Min arg0) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void meet(MultiProjection arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Namespace arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Not arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(LeftJoin arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Or arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Order arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(OrderElem arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Projection p) throws Exception {
		p.getArg().visit(this);
		p.getProjectionElemList().visit(this);
	}

	@Override
	public void meet(ProjectionElemList pel) throws Exception {
		for (ProjectionElem pe : pel.getElements())
			pe.visit(this);
	}

	@Override
	public void meet(ProjectionElem pe) throws Exception {
		selectedVars.add(nameNodeMap.get(pe.getSourceName()));
	}

	@Override
	public void meet(Reduced arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Regex arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Slice arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(SameTerm arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Filter arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(SingletonSet arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(StatementPattern sp) throws Exception {
		Var s = sp.getSubjectVar();
		Var p = sp.getPredicateVar();
		Var o = sp.getObjectVar();
		
		graph.addEdge(getMappedNode(s), getMappedNode(o), idman.addGetID(NTriplesUtil.toNTriplesString(p.getValue())));
	}

	@Override
	public void meet(Str arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Union arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(ValueConstant arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meet(Var arg0) throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public void meetOther(QueryModelNode arg0) throws Exception {
		throw new NotImplementedException();
		
	}
	
}
