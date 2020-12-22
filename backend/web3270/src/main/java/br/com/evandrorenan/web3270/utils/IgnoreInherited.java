package br.com.evandrorenan.web3270.utils;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import br.com.evandrorenan.web3270.pcomm.MyPcommSession;

@Component
public class IgnoreInherited extends JacksonAnnotationIntrospector{

	private static final long serialVersionUID = -5006740198095136236L;
	
	@Override
	public boolean hasIgnoreMarker(AnnotatedMember m) {
		return m.getDeclaringClass() == MyPcommSession.class || super.hasIgnoreMarker(m);
	}
}