from sage.misc.lazy_import import lazy_import
lazy_import('sage.geometry.polyhedron.base', 'Polyhedron')

t=[
[0, 0, 0, 0, 0, 0, 0, 1, 0],
[0, 0, 0, 0, 0, 1, 0, 1, 1],
[0, 0, 0, 0, 0, 1, 0, 1, 0],
[0, 0, 0, 0, 1, 0, 1, 1, 0],
[0, 0, 0, 0, 1, 0, 0, 1, 0],
[0, 0, 0, 0, 1, 1, 1, 1, 1],
[0, 0, 0, 0, 1, 1, 0, 1, 0],
[0, 0, 0, 1, 0, 0, 0, 1, 1],
[0, 0, 0, 1, 0, 0, 0, 1, 0],
[0, 0, 0, 1, 0, 1, 0, 0, 1],
[0, 0, 0, 1, 1, 0, 0, 0, 0],
[0, 0, 0, 1, 1, 1, 0, 0, 0],
[0, 0, 1, 0, 0, 0, 1, 1, 0],
[0, 0, 1, 0, 0, 0, 0, 1, 0],
[0, 0, 1, 0, 0, 1, 0, 0, 0],
[0, 0, 1, 0, 1, 0, 1, 1, 0],
[0, 0, 1, 0, 1, 1, 0, 0, 0],
[0, 0, 1, 1, 0, 0, 1, 1, 1],
[0, 0, 1, 1, 0, 0, 0, 1, 0],
[0, 0, 1, 1, 0, 1, 0, 0, 0],
[0, 0, 1, 1, 1, 0, 0, 0, 0],
[0, 0, 1, 1, 1, 1, 0, 0, 0],
[0, 1, 0, 0, 0, 0, 0, 1, 1],
[0, 1, 0, 0, 0, 1, 0, 1, 1],
[0, 1, 0, 0, 1, 0, 1, 1, 1],
[0, 1, 0, 0, 1, 0, 0, 1, 1],
[0, 1, 0, 0, 1, 1, 1, 1, 1],
[0, 1, 0, 0, 1, 1, 0, 1, 1],
[0, 1, 0, 1, 0, 0, 0, 1, 1],
[0, 1, 0, 1, 0, 1, 0, 0, 1],
[0, 1, 0, 1, 1, 0, 0, 0, 0],
[0, 1, 0, 1, 1, 1, 0, 0, 0],
[0, 1, 1, 0, 0, 0, 1, 1, 1],
[0, 1, 1, 0, 0, 0, 0, 1, 1],
[0, 1, 1, 0, 0, 1, 0, 0, 0],
[0, 1, 1, 0, 1, 0, 1, 1, 1],
[0, 1, 1, 0, 1, 1, 0, 0, 0],
[0, 1, 1, 1, 0, 0, 1, 1, 1],
[0, 1, 1, 1, 0, 0, 0, 1, 1],
[0, 1, 1, 1, 0, 1, 0, 0, 0],
[0, 1, 1, 1, 1, 0, 0, 0, 0],
[0, 1, 1, 1, 1, 1, 0, 0, 0],
[1, 0, 0, 0, 0, 0, 1, 1, 0],
[1, 0, 0, 0, 0, 1, 1, 1, 1],
[1, 0, 0, 0, 0, 1, 1, 1, 0],
[1, 0, 0, 0, 1, 0, 1, 1, 0],
[1, 0, 0, 0, 1, 1, 1, 1, 1],
[1, 0, 0, 0, 1, 1, 1, 1, 0],
[1, 0, 0, 1, 0, 0, 1, 1, 1],
[1, 0, 0, 1, 0, 0, 1, 1, 0],
[1, 0, 0, 1, 0, 1, 1, 0, 1],
[1, 0, 0, 1, 1, 0, 0, 0, 0],
[1, 0, 0, 1, 1, 1, 0, 0, 0],
[1, 0, 1, 0, 0, 0, 1, 1, 0],
[1, 0, 1, 0, 0, 1, 0, 0, 0],
[1, 0, 1, 0, 1, 0, 1, 1, 0],
[1, 0, 1, 0, 1, 1, 0, 0, 0],
[1, 0, 1, 1, 0, 0, 1, 1, 1],
[1, 0, 1, 1, 0, 0, 1, 1, 0],
[1, 0, 1, 1, 0, 1, 0, 0, 0],
[1, 0, 1, 1, 1, 0, 0, 0, 0],
[1, 0, 1, 1, 1, 1, 0, 0, 0],
[1, 1, 0, 0, 0, 0, 1, 1, 1],
[1, 1, 0, 0, 0, 1, 1, 1, 1],
[1, 1, 0, 0, 1, 0, 1, 1, 1],
[1, 1, 0, 0, 1, 1, 1, 1, 1],
[1, 1, 0, 1, 0, 0, 1, 1, 1],
[1, 1, 0, 1, 0, 1, 1, 0, 1],
[1, 1, 0, 1, 1, 0, 0, 0, 0],
[1, 1, 0, 1, 1, 1, 0, 0, 0],
[1, 1, 1, 0, 0, 0, 1, 1, 1],
[1, 1, 1, 0, 0, 1, 0, 0, 0],
[1, 1, 1, 0, 1, 0, 1, 1, 1],
[1, 1, 1, 0, 1, 1, 0, 0, 0],
[1, 1, 1, 1, 0, 0, 1, 1, 1],
[1, 1, 1, 1, 0, 1, 0, 0, 0],
[1, 1, 1, 1, 1, 0, 0, 0, 0],
[1, 1, 1, 1, 1, 1, 0, 0, 0]]



file=open('XooSboxCond.txt','w')
poly_test = Polyhedron(vertices = t)
for v in poly_test.Hrepresentation():
    #print (v)
    file.write(str(v)+'\n')
file.close()
