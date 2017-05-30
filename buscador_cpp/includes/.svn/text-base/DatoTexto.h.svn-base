#if !defined(_DATO_TEXTO_H)
#define _DATO_TEXTO_H

#include "Dato.h"

#include <string.h>

class DatoTexto : public Dato{
public:
	int n;
	char *s;
	
	DatoTexto();
	DatoTexto(const char *_s);
	Dato *clonar();
	~DatoTexto();
	
	char *getTexto() const;
	int largo() const;
	
	char *texto_print();
};

ostream& operator<<(ostream& os, const DatoTexto& dato);

#endif

