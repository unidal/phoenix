package com.dianping.kernel.plugin;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class CompositeDirContext implements DirContext {
	private DirContext appContext;
	private DirContext kernelContext;

	public CompositeDirContext(DirContext appContext, DirContext kernelContext) {
		this.appContext = appContext;
		this.kernelContext = kernelContext;
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal)
			throws NamingException {
		try {
			return this.appContext.addToEnvironment(propName, propVal);
		} catch (NamingException e) {
			try {
				return this.kernelContext.addToEnvironment(propName, propVal);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		try {
			this.appContext.bind(name, obj);
		} catch (NamingException e) {
			try {
				this.kernelContext.bind(name, obj);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void bind(Name name, Object obj, Attributes attrs)
			throws NamingException {
		try {
			this.appContext.bind(name, obj, attrs);
		} catch (NamingException e) {
			try {
				this.kernelContext.bind(name, obj, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		try {
			this.appContext.bind(name, obj);
		} catch (NamingException e) {
			try {
				this.kernelContext.bind(name, obj);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void bind(String name, Object obj, Attributes attrs)
			throws NamingException {
		try {
			this.appContext.bind(name, obj, attrs);
		} catch (NamingException e) {
			try {
				this.kernelContext.bind(name, obj, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void close() throws NamingException {
		try {
			this.appContext.close();
		} catch (NamingException e) {
			try {
				this.kernelContext.close();
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		try {
			return this.appContext.composeName(name, prefix);
		} catch (NamingException e) {
			try {
				return this.kernelContext.composeName(name, prefix);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public String composeName(String name, String prefix)
			throws NamingException {
		try {
			return this.appContext.composeName(name, prefix);
		} catch (NamingException e) {
			try {
				return this.kernelContext.composeName(name, prefix);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		try {
			return this.appContext.createSubcontext(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.createSubcontext(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public DirContext createSubcontext(Name name, Attributes attrs)
			throws NamingException {
		try {
			return this.appContext.createSubcontext(name, attrs);
		} catch (NamingException e) {
			try {
				return this.kernelContext.createSubcontext(name, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		try {
			return this.appContext.createSubcontext(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.createSubcontext(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public DirContext createSubcontext(String name, Attributes attrs)
			throws NamingException {
		try {
			return this.appContext.createSubcontext(name, attrs);
		} catch (NamingException e) {
			try {
				return this.kernelContext.createSubcontext(name, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		try {
			this.appContext.destroySubcontext(name);
		} catch (NamingException e) {
			try {
				this.kernelContext.destroySubcontext(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		try {
			this.appContext.destroySubcontext(name);
		} catch (NamingException e) {
			try {
				this.kernelContext.destroySubcontext(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Attributes getAttributes(Name name) throws NamingException {
		try {
			return this.appContext.getAttributes(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getAttributes(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Attributes getAttributes(Name name, String[] attrIds)
			throws NamingException {
		try {
			return this.appContext.getAttributes(name, attrIds);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getAttributes(name, attrIds);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Attributes getAttributes(String name) throws NamingException {
		try {
			return this.appContext.getAttributes(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getAttributes(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Attributes getAttributes(String name, String[] attrIds)
			throws NamingException {
		try {
			return this.appContext.getAttributes(name, attrIds);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getAttributes(name, attrIds);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		try {
			return this.appContext.getEnvironment();
		} catch (NamingException e) {
			try {
				return this.kernelContext.getEnvironment();
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		try {
			return this.appContext.getNameInNamespace();
		} catch (NamingException e) {
			try {
				return this.kernelContext.getNameInNamespace();
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		try {
			return this.appContext.getNameParser(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getNameParser(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		try {
			return this.appContext.getNameParser(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getNameParser(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public DirContext getSchema(Name name) throws NamingException {
		try {
			return this.appContext.getSchema(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getSchema(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public DirContext getSchema(String name) throws NamingException {
		try {
			return this.appContext.getSchema(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getSchema(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public DirContext getSchemaClassDefinition(Name name)
			throws NamingException {
		try {
			return this.appContext.getSchemaClassDefinition(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getSchemaClassDefinition(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public DirContext getSchemaClassDefinition(String name)
			throws NamingException {
		try {
			return this.appContext.getSchemaClassDefinition(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.getSchemaClassDefinition(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name)
			throws NamingException {
		try {
			return this.appContext.list(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.list(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name)
			throws NamingException {
		try {
			return this.appContext.list(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.list(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name)
			throws NamingException {
		try {
			return this.appContext.listBindings(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.listBindings(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name)
			throws NamingException {
		try {
			return this.appContext.listBindings(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.listBindings(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		try {
			return this.appContext.lookup(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.lookup(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Object lookup(String name) throws NamingException {
		try {
			return this.appContext.lookup(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.lookup(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		try {
			return this.appContext.lookupLink(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.lookupLink(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		try {
			return this.appContext.lookupLink(name);
		} catch (NamingException e) {
			try {
				return this.kernelContext.lookupLink(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void modifyAttributes(Name name, int mod_op, Attributes attrs)
			throws NamingException {
		try {
			this.appContext.modifyAttributes(name, mod_op, attrs);
		} catch (NamingException e) {
			try {
				this.kernelContext.modifyAttributes(name, mod_op, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void modifyAttributes(Name name, ModificationItem[] mods)
			throws NamingException {
		try {
			this.appContext.modifyAttributes(name, mods);
		} catch (NamingException e) {
			try {
				this.kernelContext.modifyAttributes(name, mods);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void modifyAttributes(String name, int mod_op, Attributes attrs)
			throws NamingException {
		try {
			this.appContext.modifyAttributes(name, mod_op, attrs);
		} catch (NamingException e) {
			try {
				this.kernelContext.modifyAttributes(name, mod_op, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void modifyAttributes(String name, ModificationItem[] mods)
			throws NamingException {
		try {
			this.appContext.modifyAttributes(name, mods);
		} catch (NamingException e) {
			try {
				this.kernelContext.modifyAttributes(name, mods);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		try {
			this.appContext.rebind(name, obj);
		} catch (NamingException e) {
			try {
				this.kernelContext.rebind(name, obj);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void rebind(Name name, Object obj, Attributes attrs)
			throws NamingException {
		try {
			this.appContext.rebind(name, obj, attrs);
		} catch (NamingException e) {
			try {
				this.kernelContext.rebind(name, obj, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		try {
			this.appContext.rebind(name, obj);
		} catch (NamingException e) {
			try {
				this.kernelContext.rebind(name, obj);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void rebind(String name, Object obj, Attributes attrs)
			throws NamingException {
		try {
			this.appContext.rebind(name, obj, attrs);
		} catch (NamingException e) {
			try {
				this.kernelContext.rebind(name, obj, attrs);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		try {
			return this.appContext.removeFromEnvironment(propName);
		} catch (NamingException e) {
			try {
				return this.kernelContext.removeFromEnvironment(propName);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		try {
			this.appContext.rename(oldName, newName);
		} catch (NamingException e) {
			try {
				this.kernelContext.rename(oldName, newName);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		try {
			this.appContext.rename(oldName, newName);
		} catch (NamingException e) {
			try {
				this.kernelContext.rename(oldName, newName);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes matchingAttributes) throws NamingException {
		try {
			return this.appContext.search(name, matchingAttributes);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, matchingAttributes);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name,
			Attributes matchingAttributes, String[] attributesToReturn)
			throws NamingException {
		try {
			return this.appContext.search(name, matchingAttributes,
					attributesToReturn);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, matchingAttributes,
						attributesToReturn);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filterExpr,
			Object[] filterArgs, SearchControls cons) throws NamingException {
		try {
			return this.appContext.search(name, filterExpr, filterArgs, cons);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, filterExpr, filterArgs,
						cons);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(Name name, String filter,
			SearchControls cons) throws NamingException {
		try {
			return this.appContext.search(name, filter, cons);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, filter, cons);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes) throws NamingException {
		try {
			return this.appContext.search(name, matchingAttributes);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, matchingAttributes);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			Attributes matchingAttributes, String[] attributesToReturn)
			throws NamingException {
		try {
			return this.appContext.search(name, matchingAttributes,
					attributesToReturn);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, matchingAttributes,
						attributesToReturn);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name,
			String filterExpr, Object[] filterArgs, SearchControls cons)
			throws NamingException {
		try {
			return this.appContext.search(name, filterExpr, filterArgs, cons);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, filterExpr, filterArgs,
						cons);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter,
			SearchControls cons) throws NamingException {
		try {
			return this.appContext.search(name, filter, cons);
		} catch (NamingException e) {
			try {
				return this.kernelContext.search(name, filter, cons);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void unbind(Name name) throws NamingException {
		try {
			this.appContext.unbind(name);
		} catch (NamingException e) {
			try {
				this.kernelContext.unbind(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}

	@Override
	public void unbind(String name) throws NamingException {
		try {
			this.appContext.unbind(name);
		} catch (NamingException e) {
			try {
				this.kernelContext.unbind(name);
			} catch (NamingException e1) {
				throw e;
			}
		}
	}
}