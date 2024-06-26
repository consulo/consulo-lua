/*
 * Copyright 2010 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sylvanaar.idea.lua.lang.parser.kahlua;


import java.util.Hashtable;

import consulo.logging.Logger;
import com.sylvanaar.idea.lua.lang.parser.LuaElementTypes;
import se.krka.kahlua.vm.Prototype;


public class FuncState {

    private static final Logger log = Logger.getInstance("Lua.Parser.FuncState");
	private static final Object NULL_OBJECT = new Object();

	/* information about local variables */
	public String[] locvars;
	/* upvalue names */
	public String[] upvalues;

	public int linedefined;
	public int lastlinedefined;
	public int isVararg;

	Prototype f;  /* current function header */
//	LTable h;  /* table to find (and reuse) elements in `k' */
	Hashtable htable;  /* table to find (and reuse) elements in `k' */
	FuncState prev;  /* enclosing function */
	KahluaParser ls;  /* lexical state */
	BlockCnt bl;  /* chain of current blocks */
	int pc;  /* next position to code (equivalent to `ncode') */
	int lasttarget;   /* `pc' of last `jump target' */
	int jpc;  /* list of pending jumps to `pc' */
	int freereg;  /* first free register */
	int nk;  /* number of elements in `k' */
	int np;  /* number of elements in `p' */
	int nlocvars;  /* number of elements in `locvars' */
	int nactvar;  /* number of active local variables */

	int[] upvalues_k = new int[LUAI_MAXUPVALUES];  /* upvalues */
	int[] upvalues_info = new int[LUAI_MAXUPVALUES];  /* upvalues */


	short actvar[] = new short[LUAI_MAXVARS];  /* declared-variable stack */

	FuncState(KahluaParser lexState) {
        Prototype f = new Prototype();
        if ( lexState.fs!=null )
            f.name = lexState.fs.f.name;
        this.f = f;

        this.prev = lexState.fs;  /* linked list of funcstates */
        this.ls = lexState;
        lexState.fs = this;

        this.pc = 0;
        this.lasttarget = -1;
        this.jpc = KahluaParser.NO_JUMP;
        this.freereg = 0;
        this.nk = 0;
        this.np = 0;
        this.nlocvars = 0;
        this.nactvar = 0;
        this.bl = null;
        f.maxStacksize = 2;  /* registers 0/1 are always valid */
        //fs.h = new LTable();
        this.htable = new Hashtable();

	}


	// =============================================================
	// from lcode.h
	// =============================================================

	InstructionPtr getcodePtr(ExpDesc e) {
		return new InstructionPtr( f.code, e.info );
	}

	int getcode(ExpDesc e) {
		return f.code[e.info];
	}

	int codeAsBx(int o, int A, int sBx) {
		return codeABx(o,A,sBx+MAXARG_sBx);
	}

	void setmultret(ExpDesc e) {
		setreturns(e, LUA_MULTRET);
	}


	// =============================================================
	// from lparser.c
	// =============================================================

	String getlocvar(int i) {
        if (i < LUAI_MAXVARS)
		    return locvars[actvar[i]];
        else
            log.debug("getlocvar attpempting to get out of bounds index");

        return null;
	}

	boolean checklimit(int v, int l, String msg) {
		if ( v > l )
			return errorlimit( l, msg );

        return true;
	}

	boolean errorlimit (int limit, String what) {
	  	String msg = (linedefined == 0) ?
	  	    "main function has more than "+limit+" "+what :
	  	    "function at line "+linedefined+" has more than "+limit+" "+what;
	  	  ls.lexerror(msg, LuaElementTypes.EMPTY_INPUT);

        return false;
	}


	int indexupvalue(String name, ExpDesc v) {
		/* new one */
		if (!checklimit(f.numUpvalues + 1, LUAI_MAXUPVALUES, "upvalues"))
            return -1;

		int i;
		for (i = 0; i < f.numUpvalues; i++) {
			if (upvalues_k[i] == v.k && upvalues_info[i] == v.info) {
				_assert(upvalues[i].equals(name));
				return i;
			}
		}
		if ( upvalues == null || f.numUpvalues + 1 > upvalues.length)
			upvalues = realloc( upvalues, f.numUpvalues*2+1 );
		upvalues[f.numUpvalues] = name;
		_assert (v.k == KahluaParser.VLOCAL || v.k == KahluaParser.VUPVAL);

		int numUpvalues = f.numUpvalues;
		f.numUpvalues++;

        upvalues_k[numUpvalues] =  (v.k);
        upvalues_info[numUpvalues] = (v.info);
		return numUpvalues;
	}

	int searchvar(String n) {
		int i;
		for (i = nactvar - 1; i >= 0; i--) {
			if (n != null && n.equals(getlocvar(i)))
				return i;
		}
		return -1; /* not found */
	}

	void markupval(int level) {
		BlockCnt bl = this.bl;
		while (bl != null && bl.nactvar > level)
			bl = bl.previous;
		if (bl != null)
			bl.upval = true;
	}

	int singlevaraux(String n, ExpDesc var, int base) {
		int v = searchvar(n); /* look up at current level */
		if (v >= 0) {
			var.init(KahluaParser.VLOCAL, v);
			if (base == 0)
				markupval(v); /* local will be used as an upval */
			return KahluaParser.VLOCAL;
		} else { /* not found at current level; try upper one */
			if (prev == null) { /* no more levels? */
				/* default is global variable */
				var.init(KahluaParser.VGLOBAL, NO_REG);
				return KahluaParser.VGLOBAL;
			}
			if (prev.singlevaraux(n, var, 0) == KahluaParser.VGLOBAL)
				return KahluaParser.VGLOBAL;
			var.info = indexupvalue(n, var); /* else was LOCAL or UPVAL */
			var.k = KahluaParser.VUPVAL; /* upvalue in this level */
			return KahluaParser.VUPVAL;
		}
	}

	void enterblock (BlockCnt bl, boolean isbreakable) {
	  bl.breaklist = KahluaParser.NO_JUMP;
	  bl.isbreakable = isbreakable;
	  bl.nactvar = this.nactvar;
	  bl.upval = false;
	  bl.previous = this.bl;
	  this.bl = bl;
	  _assert(this.freereg == this.nactvar);
	}

	//
//	void leaveblock (FuncState *fs) {
//	  BlockCnt *bl = this.bl;
//	  this.bl = bl.previous;
//	  removevars(this.ls, bl.nactvar);
//	  if (bl.upval)
//	    this.codeABC(OP_CLOSE, bl.nactvar, 0, 0);
//	  /* a block either controls scope or breaks (never both) */
//	  assert(!bl.isbreakable || !bl.upval);
//	  assert(bl.nactvar == this.nactvar);
//	  this.freereg = this.nactvar;  /* free registers */
//	  this.patchtohere(bl.breaklist);
//	}

	void leaveblock() {
		BlockCnt bl = this.bl;
		this.bl = bl.previous;
		ls.removevars(bl.nactvar);
		if (bl.upval)
			this.codeABC(OP_CLOSE, bl.nactvar, 0, 0);
		/* a block either controls scope or breaks (never both) */
		_assert (!bl.isbreakable || !bl.upval);
		_assert (bl.nactvar == this.nactvar);
		this.freereg = this.nactvar; /* free registers */
		this.patchtohere(bl.breaklist);
	}

	void closelistfield(ConsControl cc) {
		if (cc.v.k == KahluaParser.VVOID)
			return; /* there is no list item */
		this.exp2nextreg(cc.v);
		cc.v.k = KahluaParser.VVOID;
		if (cc.tostore == LFIELDS_PER_FLUSH) {
			this.setlist(cc.t.info, cc.na, cc.tostore); /* flush */
			cc.tostore = 0; /* no more items pending */
		}
	}

	boolean hasmultret(int k) {
		return ((k) == KahluaParser.VCALL || (k) == KahluaParser.VVARARG);
	}

	void lastlistfield (ConsControl cc) {
		if (cc.tostore == 0) return;
		if (hasmultret(cc.v.k)) {
		    this.setmultret(cc.v);
		    this.setlist(cc.t.info, cc.na, LUA_MULTRET);
		    cc.na--;  /** do not count last expression (unknown number of elements) */
		}
		else {
		    if (cc.v.k != KahluaParser.VVOID)
		    	this.exp2nextreg(cc.v);
		    this.setlist(cc.t.info, cc.na, cc.tostore);
		}
	}



	// =============================================================
	// from lcode.c
	// =============================================================

	void nil(int from, int n) {
		InstructionPtr previous;
		if (this.pc > this.lasttarget) { /* no jumps to current position? */
			if (this.pc == 0) { /* function start? */
				if (from >= this.nactvar)
					return; /* positions are already clean */
			} else {
				previous = new InstructionPtr(this.f.code, this.pc - 1);
				if (GET_OPCODE(previous.get()) == OP_LOADNIL) {
					int pfrom = GETARG_A(previous.get());
					int pto = GETARG_B(previous.get());
					if (pfrom <= from && from <= pto + 1) { /* can connect both? */
						if (from + n - 1 > pto)
							SETARG_B(previous, from + n - 1);
						return;
					}
				}
			}
		}
		/* else no optimization */
		this.codeABC(OP_LOADNIL, from, from + n - 1, 0);
	}


	int jump() {
		int jpc = this.jpc; /* save list of jumps to here */
		this.jpc = KahluaParser.NO_JUMP;
		int j = this.codeAsBx(OP_JMP, 0, KahluaParser.NO_JUMP);
		j = this.concat(j, jpc); /* keep them on hold */
		return j;
	}

	void ret(int first, int nret) {
		this.codeABC(OP_RETURN, first, nret + 1, 0);
	}

	int condjump(int /* OpCode */op, int A, int B, int C) {
		this.codeABC(op, A, B, C);
		return this.jump();
	}

	void fixjump(int pc, int dest) {
		InstructionPtr jmp = new InstructionPtr(this.f.code, pc);
		int offset = dest - (pc + 1);
		_assert (dest != KahluaParser.NO_JUMP);
		if (Math.abs(offset) > MAXARG_sBx)
			ls.syntaxerror("control structure too long");
		SETARG_sBx(jmp, offset);
	}


	/*
	 * * returns current `pc' and marks it as a jump target (to avoid wrong *
	 * optimizations with consecutive instructions not in the same basic block).
	 */
	int getlabel() {
		this.lasttarget = this.pc;
		return this.pc;
	}


	int getjump(int pc) {
        try {
            int offset = GETARG_sBx(this.f.code[pc]);
            /* point to itself represents end of list */
            if (offset == KahluaParser.NO_JUMP)
                /* end of list */
                return KahluaParser.NO_JUMP;
            else
                /* turn offset into absolute position */
                return (pc + 1) + offset;
        } catch (Exception e) {
            return KahluaParser.NO_JUMP;
        }
	}


    InstructionPtr getjumpcontrol(int pc) {
        InstructionPtr pi = new InstructionPtr(this.f.code, pc);

//        if (pi.code.length == pi.idx)
//            log.warn("Jump control will attempt out of bounds index");


        try {
            if (pc >= 1 && pi.code.length < pi.idx && testTMode(GET_OPCODE(pi.code[pi.idx - 1])))

                return new InstructionPtr(pi.code, pi.idx - 1);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            log.debug("bad code pointer " + pc + " instruction " + pi);
        }

        return pi;
    }


	/*
	 * * check whether list has any jump that do not produce a value * (or
	 * produce an inverted value)
	 */
	boolean need_value(int list) {
		for (; list != KahluaParser.NO_JUMP; list = this.getjump(list)) {
			int i = this.getjumpcontrol(list).get();
			if (GET_OPCODE(i) != OP_TESTSET)
				return true;
		}
		return false; /* not found */
	}


	boolean patchtestreg(int node, int reg) {
		InstructionPtr i = this.getjumpcontrol(node);
		if (GET_OPCODE(i.get()) != OP_TESTSET)
			/* cannot patch other instructions */
			return false;
		if (reg != NO_REG && reg != GETARG_B(i.get()))
			SETARG_A(i, reg);
		else
			/* no register to put value or register already has the value */
			i.set(CREATE_ABC(OP_TEST, GETARG_B(i.get()), 0, GETARG_C(i.get())));

		return true;
	}


	void removevalues(int list) {
		for (; list != KahluaParser.NO_JUMP; list = this.getjump(list))
			this.patchtestreg(list, NO_REG);
	}

	void patchlistaux(int list, int vtarget, int reg, int dtarget) {
		while (list != KahluaParser.NO_JUMP) {
			int next = this.getjump(list);
			if (this.patchtestreg(list, reg))
				this.fixjump(list, vtarget);
			else
				this.fixjump(list, dtarget); /* jump to default target */
			list = next;
		}
	}

	void dischargejpc() {
		this.patchlistaux(this.jpc, this.pc, NO_REG, this.pc);
		this.jpc = KahluaParser.NO_JUMP;
	}

	void patchlist(int list, int target) {
		if (target == this.pc)
			this.patchtohere(list);
		else {
			_assert (target < this.pc);
			this.patchlistaux(list, target, NO_REG, target);
		}
	}

	void patchtohere(int list) {
		this.getlabel();
		this.jpc = this.concat(this.jpc, list);
	}

	int concat(int l1, int l2) {
		if (l2 == KahluaParser.NO_JUMP)
			return l1;
		if (l1 == KahluaParser.NO_JUMP)
			l1 = l2;
		else {
			int list = l1;
			int next;
			while ((next = this.getjump(list)) != KahluaParser.NO_JUMP)
				/* find last element */
				list = next;
			this.fixjump(list, l2);
		}
		return l1;
	}

	void checkstack(int n) {
		int newstack = this.freereg + n;
		if (newstack > this.f.maxStacksize) {
			if (newstack >= MAXSTACK)
				ls.syntaxerror("function or expression too complex");
			this.f.maxStacksize = newstack;
		}
	}

	void reserveregs(int n) {
		this.checkstack(n);
		this.freereg += n;
	}

	void freereg(int reg) {
		if (!ISK(reg) && reg >= this.nactvar) {
			this.freereg--;
			_assert (reg == this.freereg);
		}
	}

	void freeexp(ExpDesc e) {
		if (e.k == KahluaParser.VNONRELOC)
			this.freereg(e.info);
	}

	int addk(Object v) {
		int idx;
        if (v == null) return 0;
        
		if (this.htable.containsKey(v)) {
			idx = ((Integer) htable.get(v)).intValue();
		} else {
			idx = this.nk;
			this.htable.put(v, new Integer(idx));
			final Prototype f = this.f;
			if (f.constants == null || nk + 1 >= f.constants.length)
				f.constants = realloc( f.constants, nk*2 + 1 );
			if (v == NULL_OBJECT) {
				v = null;
			}
			f.constants[this.nk++] = v;
		}
		return idx;
	}

	int stringK(String s) {
		return this.addk(s);
	}

	int numberK(double r) {
		return this.addk(new Double(r));
	}

	int boolK(boolean b) {
		return this.addk((b ? Boolean.TRUE : Boolean.FALSE));
	}

	int nilK() {
		return this.addk(NULL_OBJECT);
	}

	void setreturns(ExpDesc e, int nresults) {
		if (e.k == KahluaParser.VCALL) { /* expression is an open function call? */
			SETARG_C(this.getcodePtr(e), nresults + 1);
		} else if (e.k == KahluaParser.VVARARG) {
			SETARG_B(this.getcodePtr(e), nresults + 1);
			SETARG_A(this.getcodePtr(e), this.freereg);
			this.reserveregs(1);
		}
	}

	void setoneret(ExpDesc e) {
		if (e.k == KahluaParser.VCALL) { /* expression is an open function call? */
			e.k = KahluaParser.VNONRELOC;
			e.info = GETARG_A(this.getcode(e));
		} else if (e.k == KahluaParser.VVARARG) {
			SETARG_B(this.getcodePtr(e), 2);
			e.k = KahluaParser.VRELOCABLE; /* can relocate its simple result */
		}
	}

	void dischargevars(ExpDesc e) {
		switch (e.k) {
		case KahluaParser.VLOCAL: {
			e.k = KahluaParser.VNONRELOC;
			break;
		}
		case KahluaParser.VUPVAL: {
			e.info = this.codeABC(OP_GETUPVAL, 0, e.info, 0);
			e.k = KahluaParser.VRELOCABLE;
			break;
		}
		case KahluaParser.VGLOBAL: {
			e.info = this.codeABx(OP_GETGLOBAL, 0, e.info);
			e.k = KahluaParser.VRELOCABLE;
			break;
		}
		case KahluaParser.VINDEXED: {
			this.freereg(e.aux);
			this.freereg(e.info);
			e.info = this
					.codeABC(OP_GETTABLE, 0, e.info, e.aux);
			e.k = KahluaParser.VRELOCABLE;
			break;
		}
		case KahluaParser.VVARARG:
		case KahluaParser.VCALL: {
			this.setoneret(e);
			break;
		}
		default:
			break; /* there is one value available (somewhere) */
		}
	}

	int code_label(int A, int b, int jump) {
		this.getlabel(); /* those instructions may be jump targets */
		return this.codeABC(OP_LOADBOOL, A, b, jump);
	}

	void discharge2reg(ExpDesc e, int reg) {
		this.dischargevars(e);
		switch (e.k) {
		case KahluaParser.VNIL: {
			this.nil(reg, 1);
			break;
		}
		case KahluaParser.VFALSE:
		case KahluaParser.VTRUE: {
			this.codeABC(OP_LOADBOOL, reg, (e.k == KahluaParser.VTRUE ? 1 : 0),
					0);
			break;
		}
		case KahluaParser.VK: {
			this.codeABx(OP_LOADK, reg, e.info);
			break;
		}
		case KahluaParser.VKNUM: {
			this.codeABx(OP_LOADK, reg, this.numberK(e.nval()));
			break;
		}
		case KahluaParser.VRELOCABLE: {
			InstructionPtr pc = this.getcodePtr(e);
			SETARG_A(pc, reg);
			break;
		}
		case KahluaParser.VNONRELOC: {
			if (reg != e.info)
				this.codeABC(OP_MOVE, reg, e.info, 0);
			break;
		}
		default: {
			_assert (e.k == KahluaParser.VVOID || e.k == KahluaParser.VJMP);
			return; /* nothing to do... */
		}
		}
		e.info = reg;
		e.k = KahluaParser.VNONRELOC;
	}

	void discharge2anyreg(ExpDesc e) {
		if (e.k != KahluaParser.VNONRELOC) {
			this.reserveregs(1);
			this.discharge2reg(e, this.freereg - 1);
		}
	}

	void exp2reg(ExpDesc e, int reg) {
		this.discharge2reg(e, reg);
		if (e.k == KahluaParser.VJMP)
			e.t = this.concat(e.t, e.info); /* put this jump in `t' list */
		if (e.hasjumps()) {
			int _final; /* position after whole expression */
			int p_f = KahluaParser.NO_JUMP; /* position of an eventual LOAD false */
			int p_t = KahluaParser.NO_JUMP; /* position of an eventual LOAD true */
			if (this.need_value(e.t) || this.need_value(e.f)) {
				int fj = (e.k == KahluaParser.VJMP) ? KahluaParser.NO_JUMP : this
						.jump();
				p_f = this.code_label(reg, 0, 1);
				p_t = this.code_label(reg, 1, 0);
				this.patchtohere(fj);
			}
			_final = this.getlabel();
			this.patchlistaux(e.f, _final, reg, p_f);
			this.patchlistaux(e.t, _final, reg, p_t);
		}
		e.f = e.t = KahluaParser.NO_JUMP;
		e.info = reg;
		e.k = KahluaParser.VNONRELOC;
	}

	void exp2nextreg(ExpDesc e) {
		this.dischargevars(e);
		this.freeexp(e);
		this.reserveregs(1);
		this.exp2reg(e, this.freereg - 1);
	}

	int exp2anyreg(ExpDesc e) {
		this.dischargevars(e);
		if (e.k == KahluaParser.VNONRELOC) {
			if (!e.hasjumps())
				return e.info; /* exp is already in a register */
			if (e.info >= this.nactvar) { /* reg. is not a local? */
				this.exp2reg(e, e.info); /* put value on it */
				return e.info;
			}
		}
		this.exp2nextreg(e); /* default */
		return e.info;
	}

	void exp2val(ExpDesc e) {
		if (e.hasjumps())
			this.exp2anyreg(e);
		else
			this.dischargevars(e);
	}

	int exp2RK(ExpDesc e) {
		this.exp2val(e);
		switch (e.k) {
		case KahluaParser.VKNUM:
		case KahluaParser.VTRUE:
		case KahluaParser.VFALSE:
		case KahluaParser.VNIL: {
			if (this.nk <= MAXINDEXRK) { /* constant fit in RK operand? */
				e.info = (e.k == KahluaParser.VNIL) ? this.nilK()
						: (e.k == KahluaParser.VKNUM) ? this.numberK(e.nval())
								: this.boolK((e.k == KahluaParser.VTRUE));
				e.k = KahluaParser.VK;
				return RKASK(e.info);
			} else
				break;
		}
		case KahluaParser.VK: {
			if (e.info <= MAXINDEXRK) /* constant fit in argC? */
				return RKASK(e.info);
			else
				break;
		}
		default:
			break;
		}
		/* not a constant in the right range: put it in a register */
		return this.exp2anyreg(e);
	}

	void storevar(ExpDesc var, ExpDesc ex) {
		switch (var.k) {
		case KahluaParser.VLOCAL: {
			this.freeexp(ex);
			this.exp2reg(ex, var.info);
			return;
		}
		case KahluaParser.VUPVAL: {
			int e = this.exp2anyreg(ex);
			this.codeABC(OP_SETUPVAL, e, var.info, 0);
			break;
		}
		case KahluaParser.VGLOBAL: {
			int e = this.exp2anyreg(ex);
			this.codeABx(OP_SETGLOBAL, e, var.info);
			break;
		}
		case KahluaParser.VINDEXED: {
			int e = this.exp2RK(ex);
			this.codeABC(OP_SETTABLE, var.info, var.aux, e);
			break;
		}
		default: {
			_assert (false); /* invalid var kind to store */
			break;
		}
		}
		this.freeexp(ex);
	}

	void self(ExpDesc e, ExpDesc key) {
		int func;
		this.exp2anyreg(e);
		this.freeexp(e);
		func = this.freereg;
		this.reserveregs(2);
		this.codeABC(OP_SELF, func, e.info, this.exp2RK(key));
		this.freeexp(key);
		e.info = func;
		e.k = KahluaParser.VNONRELOC;
	}

	void invertjump(ExpDesc e) {
		InstructionPtr pc = this.getjumpcontrol(e.info);
		_assert (testTMode(GET_OPCODE(pc.get()))
				&& GET_OPCODE(pc.get()) != OP_TESTSET && GET_OPCODE(pc.get()) != OP_TEST);
		// SETARG_A(pc, !(GETARG_A(pc.get())));
		int a = GETARG_A(pc.get());
		int nota = (a!=0? 0: 1);
		SETARG_A(pc, nota);
	}

	int jumponcond(ExpDesc e, int cond) {
		if (e.k == KahluaParser.VRELOCABLE) {
			int ie = this.getcode(e);
			if (GET_OPCODE(ie) == OP_NOT) {
				this.pc--; /* remove previous OP_NOT */
				return this.condjump(OP_TEST, GETARG_B(ie), 0, (cond!=0? 0: 1));
			}
			/* else go through */
		}
		this.discharge2anyreg(e);
		this.freeexp(e);
		return this.condjump(OP_TESTSET, NO_REG, e.info, cond);
	}

	void goiftrue(ExpDesc e) {
		int pc; /* pc of last jump */
		this.dischargevars(e);
		switch (e.k) {
		case KahluaParser.VK:
		case KahluaParser.VKNUM:
		case KahluaParser.VTRUE: {
			pc = KahluaParser.NO_JUMP; /* always true; do nothing */
			break;
		}
		case KahluaParser.VFALSE: {
			pc = this.jump(); /* always jump */
			break;
		}
		case KahluaParser.VJMP: {
			this.invertjump(e);
			pc = e.info;
			break;
		}
		default: {
			pc = this.jumponcond(e, 0);
			break;
		}
		}
		e.f = this.concat(e.f, pc); /* insert last jump in `f' list */
		this.patchtohere(e.t);
		e.t = KahluaParser.NO_JUMP;
	}

	void goiffalse(ExpDesc e) {
		int pc; /* pc of last jump */
		this.dischargevars(e);
		switch (e.k) {
		case KahluaParser.VNIL:
		case KahluaParser.VFALSE: {
			pc = KahluaParser.NO_JUMP; /* always false; do nothing */
			break;
		}
		case KahluaParser.VTRUE: {
			pc = this.jump(); /* always jump */
			break;
		}
		case KahluaParser.VJMP: {
			pc = e.info;
			break;
		}
		default: {
			pc = this.jumponcond(e, 1);
			break;
		}
		}
		e.t = this.concat(e.t, pc); /* insert last jump in `t' list */
		this.patchtohere(e.f);
		e.f = KahluaParser.NO_JUMP;
	}

	void codenot(ExpDesc e) {
		this.dischargevars(e);
		switch (e.k) {
		case KahluaParser.VNIL:
		case KahluaParser.VFALSE: {
			e.k = KahluaParser.VTRUE;
			break;
		}
		case KahluaParser.VK:
		case KahluaParser.VKNUM:
		case KahluaParser.VTRUE: {
			e.k = KahluaParser.VFALSE;
			break;
		}
		case KahluaParser.VJMP: {
			this.invertjump(e);
			break;
		}
		case KahluaParser.VRELOCABLE:
		case KahluaParser.VNONRELOC: {
			this.discharge2anyreg(e);
			this.freeexp(e);
			e.info = this.codeABC(OP_NOT, 0, e.info, 0);
			e.k = KahluaParser.VRELOCABLE;
			break;
		}
		default: {
			_assert (false); /* cannot happen */
			break;
		}
		}
		/* interchange true and false lists */
		{
			int temp = e.f;
			e.f = e.t;
			e.t = temp;
		}
		this.removevalues(e.f);
		this.removevalues(e.t);
	}

	void indexed(ExpDesc t, ExpDesc k) {
		t.aux = this.exp2RK(k);
		t.k = KahluaParser.VINDEXED;
	}

	boolean constfolding(int op, ExpDesc e1, ExpDesc e2) {
		if (!e1.isnumeral() || !e2.isnumeral())
			return false;
		double v1, v2, r;
		v1 = e1.nval();
		v2 = e2.nval();
		switch (op) {
		case OP_ADD:
			r = v1 + v2;
			break;
		case OP_SUB:
			r = v1 - v2;
			break;
		case OP_MUL:
			r = v1 * v2;
			break;
		case OP_DIV:
			r = v1 / v2;
			break;
		case OP_MOD:
			r = v1 % v2;
			break;
		case OP_POW:
			return false;
		case OP_UNM:
			r = -v1;
			break;
		case OP_LEN:
			return false; /* no constant folding for 'len' */
		default:
			_assert (false);
		    return false;
		}
		if (Double.isNaN(r) || Double.isInfinite(r))
			return false; /* do not attempt to produce NaN */
		e1.setNval( r);
		return true;
	}

	void codearith(int op, ExpDesc e1, ExpDesc e2) {
		if (constfolding(op, e1, e2))
			return;
		else {
			int o2 = (op != OP_UNM && op != OP_LEN) ? this.exp2RK(e2)
					: 0;
			int o1 = this.exp2RK(e1);
			if (o1 > o2) {
				this.freeexp(e1);
				this.freeexp(e2);
			} else {
				this.freeexp(e2);
				this.freeexp(e1);
			}
			e1.info = this.codeABC(op, 0, o1, o2);
			e1.k = KahluaParser.VRELOCABLE;
		}
	}

	void codecomp(int /* OpCode */op, int cond, ExpDesc e1, ExpDesc e2) {
		int o1 = this.exp2RK(e1);
		int o2 = this.exp2RK(e2);
		this.freeexp(e2);
		this.freeexp(e1);
		if (cond == 0 && op != OP_EQ) {
			int temp; /* exchange args to replace by `<' or `<=' */
			temp = o1;
			o1 = o2;
			o2 = temp; /* o1 <==> o2 */
			cond = 1;
		}
		e1.info = this.condjump(op, cond, o1, o2);
		e1.k = KahluaParser.VJMP;
	}

	void prefix(int /* UnOpr */op, ExpDesc e) {
		ExpDesc e2 = new ExpDesc();
		e2.init(KahluaParser.VKNUM, 0);
		switch (op) {
		case KahluaParser.OPR_MINUS: {
			if (e.k == KahluaParser.VK)
				this.exp2anyreg(e); /* cannot operate on non-numeric constants */
			this.codearith(OP_UNM, e, e2);
			break;
		}
		case KahluaParser.OPR_NOT:
			this.codenot(e);
			break;
		case KahluaParser.OPR_LEN: {
			this.exp2anyreg(e); /* cannot operate on constants */
			this.codearith(OP_LEN, e, e2);
			break;
		}
		default:
			_assert (false);
		}
	}

	void infix(int /* BinOpr */op, ExpDesc v) {
		switch (op) {
		case KahluaParser.OPR_AND: {
			this.goiftrue(v);
			break;
		}
		case KahluaParser.OPR_OR: {
			this.goiffalse(v);
			break;
		}
		case KahluaParser.OPR_CONCAT: {
			this.exp2nextreg(v); /* operand must be on the `stack' */
			break;
		}
		case KahluaParser.OPR_ADD:
		case KahluaParser.OPR_SUB:
		case KahluaParser.OPR_MUL:
		case KahluaParser.OPR_DIV:
		case KahluaParser.OPR_MOD:
		case KahluaParser.OPR_POW: {
			if (!v.isnumeral())
				this.exp2RK(v);
			break;
		}
		default: {
			this.exp2RK(v);
			break;
		}
		}
	}


	void posfix(int op, ExpDesc e1, ExpDesc e2) {
		switch (op) {
		case KahluaParser.OPR_AND: {
			_assert (e1.t == KahluaParser.NO_JUMP); /* list must be closed */
			this.dischargevars(e2);
			e2.f = this.concat(e2.f, e1.f);
			// *e1 = *e2;
			e1.setvalue(e2);
			break;
		}
		case KahluaParser.OPR_OR: {
			_assert (e1.f == KahluaParser.NO_JUMP); /* list must be closed */
			this.dischargevars(e2);
			e2.t = this.concat(e2.t, e1.t);
			// *e1 = *e2;
			e1.setvalue(e2);
			break;
		}
		case KahluaParser.OPR_CONCAT: {
			this.exp2val(e2);
			if (e2.k == KahluaParser.VRELOCABLE
					&& GET_OPCODE(this.getcode(e2)) == OP_CONCAT) {
				_assert (e1.info == GETARG_B(this.getcode(e2)) - 1);
				this.freeexp(e1);
				SETARG_B(this.getcodePtr(e2), e1.info);
				e1.k = KahluaParser.VRELOCABLE;
				e1.info = e2.info;
			} else {
				this.exp2nextreg(e2); /* operand must be on the 'stack' */
				this.codearith(OP_CONCAT, e1, e2);
			}
			break;
		}
		case KahluaParser.OPR_ADD:
			this.codearith(OP_ADD, e1, e2);
			break;
		case KahluaParser.OPR_SUB:
			this.codearith(OP_SUB, e1, e2);
			break;
		case KahluaParser.OPR_MUL:
			this.codearith(OP_MUL, e1, e2);
			break;
		case KahluaParser.OPR_DIV:
			this.codearith(OP_DIV, e1, e2);
			break;
		case KahluaParser.OPR_MOD:
			this.codearith(OP_MOD, e1, e2);
			break;
		case KahluaParser.OPR_POW:
			this.codearith(OP_POW, e1, e2);
			break;
		case KahluaParser.OPR_EQ:
			this.codecomp(OP_EQ, 1, e1, e2);
			break;
		case KahluaParser.OPR_NE:
			this.codecomp(OP_EQ, 0, e1, e2);
			break;
		case KahluaParser.OPR_LT:
			this.codecomp(OP_LT, 1, e1, e2);
			break;
		case KahluaParser.OPR_LE:
			this.codecomp(OP_LE, 1, e1, e2);
			break;
		case KahluaParser.OPR_GT:
			this.codecomp(OP_LT, 0, e1, e2);
			break;
		case KahluaParser.OPR_GE:
			this.codecomp(OP_LE, 0, e1, e2);
			break;
		default:
			_assert (false);
		}
	}


	void fixline(int line) {
		this.f.lines[this.pc - 1] = line;
	}


	int code(int instruction, int line) {
		Prototype f = this.f;
		this.dischargejpc(); /* `pc' will change */
		/* put new instruction in code array */
		if (f.code == null || this.pc + 1 > f.code.length)
			f.code = realloc(f.code, this.pc * 2 + 1);
		f.code[this.pc] = instruction;
		/* save corresponding line information */
		if (f.lines == null || this.pc + 1 > f.lines.length)
			f.lines = realloc(f.lines,
					this.pc * 2 + 1);
		f.lines[this.pc] = line;
		return this.pc++;
	}


	int codeABC(int o, int a, int b, int c) {
		_assert (getOpMode(o) == iABC);
		_assert (getBMode(o) != OpArgN || b == 0);
		_assert (getCMode(o) != OpArgN || c == 0);
		return this.code(CREATE_ABC(o, a, b, c), this.ls.lastline);
	}


	int codeABx(int o, int a, int bc) {
		_assert (getOpMode(o) == iABx || getOpMode(o) == iAsBx);
		_assert (getCMode(o) == OpArgN);
		return this.code(CREATE_ABx(o, a, bc), this.ls.lastline);
	}


	void setlist(int base, int nelems, int tostore) {
		int c = (nelems - 1) / LFIELDS_PER_FLUSH + 1;
		int b = (tostore == LUA_MULTRET) ? 0 : tostore;
		_assert (tostore != 0);
		if (c <= MAXARG_C)
			this.codeABC(OP_SETLIST, base, b, c);
		else {
			this.codeABC(OP_SETLIST, base, b, 0);
			this.code(c, this.ls.lastline);
		}
		this.freereg = base + 1; /* free registers with list values */
	}

	protected static void _assert(boolean b) {
//		if (!b)
//			throw new KahluaException("compiler assert failed");
	}

	public static final int MAXSTACK = 250;
	static final int LUAI_MAXUPVALUES = 60;
	static final int LUAI_MAXVARS = 200;


	/* OpArgMask */
	static final int
	  OpArgN = 0,  /* argument is not used */
	  OpArgU = 1,  /* argument is used */
	  OpArgR = 2,  /* argument is a register or a jump offset */
	  OpArgK = 3;   /* argument is a constant or register/constant */


	static void SET_OPCODE(InstructionPtr i,int o) {
		i.set( ( i.get() & (MASK_NOT_OP)) | ((o << POS_OP) & MASK_OP) );
	}

	static void SETARG_A(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_A)) | ((u << POS_A) & MASK_A) );
	}

	static void SETARG_B(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_B)) | ((u << POS_B) & MASK_B) );
	}

	static void SETARG_C(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_C)) | ((u << POS_C) & MASK_C) );
	}

	static void SETARG_Bx(InstructionPtr i,int u) {
		i.set( ( i.get() & (MASK_NOT_Bx)) | ((u << POS_Bx) & MASK_Bx) );
	}

	static void SETARG_sBx(InstructionPtr i,int u) {
		SETARG_Bx( i, u + MAXARG_sBx );
	}

	static int CREATE_ABC(int o, int a, int b, int c) {
		return ((o << POS_OP) & MASK_OP) |
				((a << POS_A) & MASK_A) |
				((b << POS_B) & MASK_B) |
				((c << POS_C) & MASK_C) ;
	}

	static int CREATE_ABx(int o, int a, int bc) {
		return ((o << POS_OP) & MASK_OP) |
				((a << POS_A) & MASK_A) |
				((bc << POS_Bx) & MASK_Bx) ;
 	}

	// vector reallocation

	static Object[] realloc(Object[] v, int n) {
		Object[] a = new Object[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static String[] realloc(String[] v, int n) {
		String[] a = new String[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static Prototype[] realloc(Prototype[] v, int n) {
		Prototype[] a = new Prototype[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static int[] realloc(int[] v, int n) {
		int[] a = new int[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}

	static byte[] realloc(byte[] v, int n) {
		byte[] a = new byte[n];
		if ( v != null )
			System.arraycopy(v, 0, a, 0, Math.min(v.length,n));
		return a;
	}


	/** use return values from previous op */
	public static final int LUA_MULTRET = -1;

	/** masks for new-style vararg */
	public static final int     VARARG_HASARG		= 1;
	public static final int     VARARG_ISVARARG	= 2;
	public static final int     VARARG_NEEDSARG	= 4;

	// from lopcodes.h

	/*===========================================================================
	  We assume that instructions are unsigned numbers.
	  All instructions have an opcode in the first 6 bits.
	  Instructions can have the following fields:
		`A' : 8 bits
		`B' : 9 bits
		`C' : 9 bits
		`Bx' : 18 bits (`B' and `C' together)
		`sBx' : signed Bx

	  A signed argument is represented in excess K; that is, the number
	  value is the unsigned value minus K. K is exactly the maximum value
	  for that argument (so that -max is represented by 0, and +max is
	  represented by 2*max), which is half the maximum for the corresponding
	  unsigned argument.
	===========================================================================*/


	/* basic instruction format */
	public static final int	iABC = 0;
	public static final int	iABx = 1;
	public static final int	iAsBx = 2;


	/*
	** size and position of opcode arguments.
	*/
	public static final int SIZE_C		= 9;
	public static final int SIZE_B		= 9;
	public static final int SIZE_Bx		= (SIZE_C + SIZE_B);
	public static final int SIZE_A		= 8;

	public static final int SIZE_OP		= 6;

	public static final int POS_OP		= 0;
	public static final int POS_A		= (POS_OP + SIZE_OP);
	public static final int POS_C		= (POS_A + SIZE_A);
	public static final int POS_B		= (POS_C + SIZE_C);
	public static final int POS_Bx		= POS_C;


	public static final int MAX_OP          = ((1<<SIZE_OP)-1);
	public static final int MAXARG_A        = ((1<<SIZE_A)-1);
	public static final int MAXARG_B        = ((1<<SIZE_B)-1);
	public static final int MAXARG_C        = ((1<<SIZE_C)-1);
	public static final int MAXARG_Bx       = ((1<<SIZE_Bx)-1);
	public static final int MAXARG_sBx      = (MAXARG_Bx>>1);     	/* `sBx' is signed */

	public static final int MASK_OP = ((1<<SIZE_OP)-1)<<POS_OP;
	public static final int MASK_A  = ((1<<SIZE_A)-1)<<POS_A;
	public static final int MASK_B  = ((1<<SIZE_B)-1)<<POS_B;
	public static final int MASK_C  = ((1<<SIZE_C)-1)<<POS_C;
	public static final int MASK_Bx = ((1<<SIZE_Bx)-1)<<POS_Bx;

	public static final int MASK_NOT_OP = ~MASK_OP;
	public static final int MASK_NOT_A  = ~MASK_A;
	public static final int MASK_NOT_B  = ~MASK_B;
	public static final int MASK_NOT_C  = ~MASK_C;
	public static final int MASK_NOT_Bx = ~MASK_Bx;

	/*
	** the following macros help to manipulate instructions
	*/
	public static int GET_OPCODE(int i) {
		return (i >> POS_OP) & MAX_OP;
	}

	public static int GETARG_A(int i) {
		return (i >> POS_A) & MAXARG_A;
	}

	public static int GETARG_B(int i) {
		return (i >> POS_B) & MAXARG_B;
	}

	public static int GETARG_C(int i) {
		return (i >> POS_C) & MAXARG_C;
	}

	public static int GETARG_Bx(int i) {
		return (i >> POS_Bx) & MAXARG_Bx;
	}

	public static int GETARG_sBx(int i) {
		return ((i >> POS_Bx) & MAXARG_Bx) - MAXARG_sBx;
	}


	/*
	** Macros to operate RK indices
	*/

	/** this bit 1 means constant (0 means register) */
	public static final int BITRK		= (1 << (SIZE_B - 1));

	/** test whether value is a constant */
	public static boolean ISK(int x) {
		return 0 != ((x) & BITRK);
	}

	/** gets the index of the constant */
	public static int INDEXK(int r) {
		return ((int)(r) & ~BITRK);
	}

	public static final int MAXINDEXRK	= (BITRK - 1);

	/** code a constant index as a RK value */
	public static int RKASK(int x) {
		return ((x) | BITRK);
	}


	/**
	** invalid register that fits in 8 bits
	*/
	public static final int  NO_REG		= MAXARG_A;


	/*
	** R(x) - register
	** Kst(x) - constant (in constant table)
	** RK(x) == if ISK(x) then Kst(INDEXK(x)) else R(x)
	*/


	/*
	** grep "ORDER OP" if you change these enums
	*/

	/*----------------------------------------------------------------------
	name		args	description
	------------------------------------------------------------------------*/
	public static final int OP_MOVE = 0;/*	A B	R(A) := R(B)					*/
	public static final int OP_LOADK = 1;/*	A Bx	R(A) := Kst(Bx)					*/
	public static final int OP_LOADBOOL = 2;/*	A B C	R(A) := (Bool)B; if (C) pc++			*/
	public static final int OP_LOADNIL = 3; /*	A B	R(A) := ... := R(B) := nil			*/
	public static final int OP_GETUPVAL = 4; /*	A B	R(A) := UpValue[B]				*/

	public static final int OP_GETGLOBAL = 5; /*	A Bx	R(A) := Gbl[Kst(Bx)]				*/
	public static final int OP_GETTABLE = 6; /*	A B C	R(A) := R(B)[RK(C)]				*/

	public static final int OP_SETGLOBAL = 7; /*	A Bx	Gbl[Kst(Bx)] := R(A)				*/
	public static final int OP_SETUPVAL = 8; /*	A B	UpValue[B] := R(A)				*/
	public static final int OP_SETTABLE = 9; /*	A B C	R(A)[RK(B)] := RK(C)				*/

	public static final int OP_NEWTABLE = 10; /*	A B C	R(A) := {} (size = B,C)				*/

	public static final int OP_SELF = 11; /*	A B C	R(A+1) := R(B); R(A) := R(B)[RK(C)]		*/

	public static final int OP_ADD = 12; /*	A B C	R(A) := RK(B) + RK(C)				*/
	public static final int OP_SUB = 13; /*	A B C	R(A) := RK(B) - RK(C)				*/
	public static final int OP_MUL = 14; /*	A B C	R(A) := RK(B) * RK(C)				*/
	public static final int OP_DIV = 15; /*	A B C	R(A) := RK(B) / RK(C)				*/
	public static final int OP_MOD = 16; /*	A B C	R(A) := RK(B) % RK(C)				*/
	public static final int OP_POW = 17; /*	A B C	R(A) := RK(B) ^ RK(C)				*/
	public static final int OP_UNM = 18; /*	A B	R(A) := -R(B)					*/
	public static final int OP_NOT = 19; /*	A B	R(A) := not R(B)				*/
	public static final int OP_LEN = 20; /*	A B	R(A) := length of R(B)				*/

	public static final int OP_CONCAT = 21; /*	A B C	R(A) := R(B).. ... ..R(C)			*/

	public static final int OP_JMP = 22; /*	sBx	pc+=sBx					*/

	public static final int OP_EQ = 23; /*	A B C	if ((RK(B) == RK(C)) ~= A) then pc++		*/
	public static final int OP_LT = 24; /*	A B C	if ((RK(B) <  RK(C)) ~= A) then pc++  		*/
	public static final int OP_LE = 25; /*	A B C	if ((RK(B) <= RK(C)) ~= A) then pc++  		*/

	public static final int OP_TEST = 26; /*	A C	if not (R(A) <=> C) then pc++			*/
	public static final int OP_TESTSET = 27; /*	A B C	if (R(B) <=> C) then R(A) := R(B) else pc++	*/

	public static final int OP_CALL = 28; /*	A B C	R(A), ... ,R(A+C-2) := R(A)(R(A+1), ... ,R(A+B-1)) */
	public static final int OP_TAILCALL = 29; /*	A B C	return R(A)(R(A+1), ... ,R(A+B-1))		*/
	public static final int OP_RETURN = 30; /*	A B	return R(A), ... ,R(A+B-2)	(see note)	*/

	public static final int OP_FORLOOP = 31; /*	A sBx	R(A)+=R(A+2);
				if R(A) <?= R(A+1) then { pc+=sBx; R(A+3)=R(A) }*/
	public static final int OP_FORPREP = 32; /*	A sBx	R(A)-=R(A+2); pc+=sBx				*/

	public static final int OP_TFORLOOP = 33; /*	A C	R(A+3), ... ,R(A+2+C) := R(A)(R(A+1), R(A+2));
	                        if R(A+3) ~= nil then R(A+2)=R(A+3) else pc++	*/
	public static final int OP_SETLIST = 34; /*	A B C	R(A)[(C-1)*FPF+i] := R(A+i), 1 <= i <= B	*/

	public static final int OP_CLOSE = 35; /*	A 	close all variables in the stack up to (>=) R(A)*/
	public static final int OP_CLOSURE = 36; /*	A Bx	R(A) := closure(KPROTO[Bx], R(A), ... ,R(A+n))	*/

	public static final int OP_VARARG = 37; /*	A B	R(A), R(A+1), ..., R(A+B-1) = vararg		*/


	public static final int NUM_OPCODES	= OP_VARARG + 1;



	/*===========================================================================
	  Notes:
	  (*) In OP_CALL, if (B == 0) then B = top. C is the number of returns - 1,
	      and can be 0: OP_CALL then sets `top' to last_result+1, so
	      next open instruction (OP_CALL, OP_RETURN, OP_SETLIST) may use `top'.

	  (*) In OP_VARARG, if (B == 0) then use actual number of varargs and
	      set top (like in OP_CALL with C == 0).

	  (*) In OP_RETURN, if (B == 0) then return up to `top'

	  (*) In OP_SETLIST, if (B == 0) then B = `top';
	      if (C == 0) then next `instruction' is real C

	  (*) For comparisons, A specifies what condition the test should accept
	      (true or false).

	  (*) All `skips' (pc++) assume that next instruction is a jump
	===========================================================================*/


	/*
	** masks for instruction properties. The format is:
	** bits 0-1: op mode
	** bits 2-3: C arg mode
	** bits 4-5: B arg mode
	** bit 6: instruction set register A
	** bit 7: operator is a test
	*/

	  public static final int[] luaP_opmodes = {
	  /*   T        A           B             C          mode		   opcode	*/
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_MOVE */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgN<<2) | (iABx),		/* OP_LOADK */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_LOADBOOL */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_LOADNIL */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_GETUPVAL */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgN<<2) | (iABx),		/* OP_GETGLOBAL */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgK<<2) | (iABC),		/* OP_GETTABLE */
		 (0<<7) | (0<<6) | (OpArgK<<4) | (OpArgN<<2) | (iABx),		/* OP_SETGLOBAL */
		 (0<<7) | (0<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_SETUPVAL */
		 (0<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_SETTABLE */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_NEWTABLE */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgK<<2) | (iABC),		/* OP_SELF */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_ADD */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_SUB */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_MUL */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_DIV */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_MOD */
		 (0<<7) | (1<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_POW */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_UNM */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_NOT */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iABC),		/* OP_LEN */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgR<<2) | (iABC),		/* OP_CONCAT */
		 (0<<7) | (0<<6) | (OpArgR<<4) | (OpArgN<<2) | (iAsBx),		/* OP_JMP */
		 (1<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_EQ */
		 (1<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_LT */
		 (1<<7) | (0<<6) | (OpArgK<<4) | (OpArgK<<2) | (iABC),		/* OP_LE */
		 (1<<7) | (1<<6) | (OpArgR<<4) | (OpArgU<<2) | (iABC),		/* OP_TEST */
		 (1<<7) | (1<<6) | (OpArgR<<4) | (OpArgU<<2) | (iABC),		/* OP_TESTSET */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_CALL */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_TAILCALL */
		 (0<<7) | (0<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_RETURN */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iAsBx),		/* OP_FORLOOP */
		 (0<<7) | (1<<6) | (OpArgR<<4) | (OpArgN<<2) | (iAsBx),		/* OP_FORPREP */
		 (1<<7) | (0<<6) | (OpArgN<<4) | (OpArgU<<2) | (iABC),		/* OP_TFORLOOP */
		 (0<<7) | (0<<6) | (OpArgU<<4) | (OpArgU<<2) | (iABC),		/* OP_SETLIST */
		 (0<<7) | (0<<6) | (OpArgN<<4) | (OpArgN<<2) | (iABC),		/* OP_CLOSE */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABx),		/* OP_CLOSURE */
		 (0<<7) | (1<<6) | (OpArgU<<4) | (OpArgN<<2) | (iABC),		/* OP_VARARG */
	  };

	public static int getOpMode(int m) {
		return luaP_opmodes[m] & 3;
	}
	public static int getBMode(int m) {
		return (luaP_opmodes[m] >> 4) & 3;
	}
	public static int getCMode(int m) {
		return (luaP_opmodes[m] >> 2) & 3;
	}
	public static boolean testTMode(int m) {
		return 0 != (luaP_opmodes[m] & (1 << 7));
	}

	/* number of list items to accumulate before a SETLIST instruction */
	public static final int LFIELDS_PER_FLUSH = 50;

}