import sys
import math

originalnaListaVrijednosti = []
originalnaListaVrijednostiCiljneVarijable = []

znacajke = []
moguceVrijednostiZnacajki = []
brojMogucihVrijednostiZnacajki = [[]]
potrebnaListaVrijednosti = []

ciljnaVarijabla = ""
moguceVrijednostiCiljneVarijable = []
brojMogucihVrijednostiCiljneVarijable = []
potrebnaListaVrijednostiCiljneVarijable = []

tablica = []

entropijaPocetnogSkupa = 0
entropijaPodskupa = []
ukupanBrojVrijednostiZnacajki = []
IG = []

ispisaneZnacajke = []

entropija = []
potrebnaEntropija = []

grane = []
stablo = []

#testovi
listaVrijednostiTest = []
listaVrijednostiCiljneVarijableTest = []
predikcijaCiljneVarijable = []


mode = ""
model = ""
maxDepth = -1
numTrees = 0
featureRatio = 0
exampleRatio = 0


def ucitavanjeDatoteka():
    global ciljnaVarijabla
    global moguceVrijednostiCiljneVarijable
    global potrebnaListaVrijednosti
    global potrebnaListaVrijednostiCiljneVarijable

    file = open(sys.argv[1], "r")
    index = 0
    for line in file:
        line = line.rstrip()
        if index == 0:
            for znacajka in line.split(","):
                znacajke.append(znacajka)
            ciljnaVarijabla = znacajke.pop()
        else:
            linijaVrijednosti = line.split(",")
            brojacVrijednosti = 0
            for vrijednost in linijaVrijednosti:
                if index == 1:
                    moguceVrijednostiZnacajki.append([])
                if vrijednost not in moguceVrijednostiZnacajki[brojacVrijednosti]:
                    moguceVrijednostiZnacajki[brojacVrijednosti].append(vrijednost)
                brojacVrijednosti += 1
            originalnaListaVrijednostiCiljneVarijable.append(linijaVrijednosti.pop())
            originalnaListaVrijednosti.append(linijaVrijednosti)
        index += 1
    moguceVrijednostiCiljneVarijable = moguceVrijednostiZnacajki.pop()

    potrebnaListaVrijednosti = originalnaListaVrijednosti
    potrebnaListaVrijednostiCiljneVarijable = originalnaListaVrijednostiCiljneVarijable


def racunanjeEntropijuPocetnogSkupa():
    global entropijaPocetnogSkupa

    for mogucaVrijednostCiljneVarijable in moguceVrijednostiCiljneVarijable:
        br = 0
        for vrijednostCiljneVarijable in originalnaListaVrijednostiCiljneVarijable:
            if vrijednostCiljneVarijable == mogucaVrijednostCiljneVarijable:
                br += 1
        p = br / len(originalnaListaVrijednostiCiljneVarijable)
        if p != 0:
            entropijaPocetnogSkupa -= p * math.log2(p)

def izbrojMoguceVrijednostiZnacajki():
    global brojMogucihVrijednostiZnacajki

    brojMogucihVrijednostiZnacajki = []

    for i in range(len(znacajke)):
        brojMogucihVrijednostiZnacajki.append([])
        for j in range(len(moguceVrijednostiZnacajki[i])):
            mogucaVrijednost = moguceVrijednostiZnacajki[i][j]
            brojacMoguceVrijednosti = 0
            for k in range(len(potrebnaListaVrijednosti)):
                if potrebnaListaVrijednosti[k][i] == mogucaVrijednost:
                    brojacMoguceVrijednosti += 1
            brojMogucihVrijednostiZnacajki[i].append(brojacMoguceVrijednosti)

def izbrojMoguceVrijednostiCiljneVarijable():
    global brojMogucihVrijednostiCiljneVarijable

    brojMogucihVrijednostiCiljneVarijable = []

    for i in range(len(moguceVrijednostiCiljneVarijable)):
        mogucaVrijednost = moguceVrijednostiCiljneVarijable[i]
        brojacMoguceVrijednosti = 0
        for j in range(len(potrebnaListaVrijednostiCiljneVarijable)):
            if potrebnaListaVrijednostiCiljneVarijable[j] == mogucaVrijednost:
                brojacMoguceVrijednosti += 1
        brojMogucihVrijednostiCiljneVarijable.append(brojacMoguceVrijednosti)

def napraviTablicu():
    global tablica

    tablica = []

    for i in range(len(znacajke)):
        if i not in ispisaneZnacajke:
            znacajka = znacajke[i]
            for mogucaVrijednostZnacajke in moguceVrijednostiZnacajki[i]:
                for mogucaVrijednostCiljneVarijable in moguceVrijednostiCiljneVarijable:
                    key = znacajka + "," + mogucaVrijednostZnacajke + "," + mogucaVrijednostCiljneVarijable
                    tablica.append([key, 0])

def popuniTablicu():
    for i in range(len(znacajke)):
        znacajka = znacajke[i]
        for j in range(len(potrebnaListaVrijednosti)):
            vrijednostZnacajke = potrebnaListaVrijednosti[j][i]
            vrijednostCiljneVarijable = potrebnaListaVrijednostiCiljneVarijable[j]
            for tuple in tablica:
                if tuple[0].split(",")[0] == znacajka and tuple[0].split(",")[1] == vrijednostZnacajke and \
                        tuple[0].split(",")[2] == vrijednostCiljneVarijable:
                    tuple[1] += 1

def prebrojiZnacajke():
    global ukupanBrojVrijednostiZnacajki

    ukupanBrojVrijednostiZnacajki = []

    for i in range(len(znacajke)):
        moguceVrijednostiZnacajke = moguceVrijednostiZnacajki[i]
        ukupanBrojVrijednostiZnacajke = []
        for j in range(len(moguceVrijednostiZnacajke)):
            mogucaVrijednostZnacajke = moguceVrijednostiZnacajke[j]
            ukupanBrojMoguceVrijednostiZnacajke = 0
            for k in range(len(potrebnaListaVrijednosti)):
                if potrebnaListaVrijednosti[k][i] == mogucaVrijednostZnacajke:
                    ukupanBrojMoguceVrijednostiZnacajke += 1
            ukupanBrojVrijednostiZnacajke.append(ukupanBrojMoguceVrijednostiZnacajke)
        ukupanBrojVrijednostiZnacajki.append(ukupanBrojVrijednostiZnacajke)


def izracunajEntropiju(entropijaPotrebnogSkupa):
    global entropija

    entropija = []

    for i in range(len(znacajke)):
        znacajka = znacajke[i]
        entropijaZnacajke = []
        for j in range(len(moguceVrijednostiZnacajki[i])):
            mogucaVrijednostZnacajke = moguceVrijednostiZnacajki[i][j]
            entropijaVrijednostiZnacajke = 0
            for k in range(len(tablica)):
                if tablica[k][0].split(",")[0] == znacajka and tablica[k][0].split(",")[1] == mogucaVrijednostZnacajke:
                    a = tablica[k][1]
                    b = ukupanBrojVrijednostiZnacajki[i][j]
                    if a != 0:
                        c = a / b
                        entropijaVrijednostiZnacajke -= c * math.log2(c)
            entropijaZnacajke.append(entropijaVrijednostiZnacajke)
        entropija.append(entropijaZnacajke)

    for mogucaVrijednostCiljneVarijable in moguceVrijednostiCiljneVarijable:
        br = 0
        for vrijednostCiljneVarijable in potrebnaListaVrijednostiCiljneVarijable:
            if vrijednostCiljneVarijable == mogucaVrijednostCiljneVarijable:
                br += 1
        p = br / len(potrebnaListaVrijednostiCiljneVarijable)
        if p != 0:
            entropijaPotrebnogSkupa -= p * math.log2(p)

def izracunajIG(razina, entropijaPotrebnogSkupa, grana):
    global IG

    IG = []

    if razina == 2:
        a = 0
    for i in range(len(znacajke)):
        ig = entropijaPotrebnogSkupa
        for j in range(len(moguceVrijednostiZnacajki[i])):
            ig -= brojMogucihVrijednostiZnacajki[i][j] / len(potrebnaListaVrijednosti) * entropija[i][j]
        IG.append([znacajke[i], ig])

def ispis(razina, ispisaneZnacajke):
    currentIG = 0
    indexi = []

    for i in range(len(IG)):
        if IG[i][1] > currentIG and i not in ispisaneZnacajke:
            currentIG = IG[i][1]

    for i in range(len(IG)):
        if IG[i][1] == currentIG and i not in ispisaneZnacajke:
            indexi.append(i)

    if len(indexi) != 0:
        if len(indexi) > 1:
            a = 9
        index = indexi[0]
        for i in range(len(indexi)):
            if znacajke[indexi[i]] < znacajke[index]:
                index = indexi[i]
        if index not in ispisaneZnacajke:
            stablo.append(str(razina) + ":" + IG[index][0])
            return index

    return -1


def updatePotrebneVrijednosti(grana):
    global potrebnaListaVrijednosti
    global potrebnaListaVrijednostiCiljneVarijable

    potrebnaListaVrijednosti = []
    potrebnaListaVrijednostiCiljneVarijable = []

    for i in range(len(originalnaListaVrijednosti)):
        redVrijednosti = originalnaListaVrijednosti[i]
        br = 0
        for j in range(len(grana)):
            if redVrijednosti[int(grana[j].split(",")[0])] == grana[j].split(",")[1]:
                br += 1
        if br == len(grana):
            potrebnaListaVrijednosti.append(originalnaListaVrijednosti[i])
            potrebnaListaVrijednostiCiljneVarijable.append(originalnaListaVrijednostiCiljneVarijable[i])


def odrediVrijednostCiljneVarijable(grana):
    indexi = []
    for i in range (len(originalnaListaVrijednosti)):
        poklapanje = 0
        primjerVrijednosti = originalnaListaVrijednosti[i]
        for j in range (len(grana)):
            indexZnacajke = int(grana[j].split(",")[0])
            vrijednostZnacajke = grana[j].split(",")[1]
            if primjerVrijednosti[indexZnacajke] == vrijednostZnacajke:
                poklapanje += 1

        if poklapanje == len(grana):
            indexi.append(i)

    if len(grane) == 92:
        a = 0
    brCV = []
    if(len(indexi)) > 1:
        for i in range(len(moguceVrijednostiCiljneVarijable)):
            br = 0
            for j in range(len(indexi)):
                if originalnaListaVrijednostiCiljneVarijable[indexi[j]] == moguceVrijednostiCiljneVarijable[i]:
                    br += 1
            brCV.append(br)

        maxIndexi = []
        maxBr = 0
        for i in range(len(brCV)):
            if brCV[i] > maxBr:
                maxBr = brCV[i]

        for i in range(len(brCV)):
            if brCV[i] == maxBr:
                maxIndexi.append(i)

        vrijednostCV = moguceVrijednostiCiljneVarijable[maxIndexi[0]]
        for i in range(len(maxIndexi)):
            if moguceVrijednostiCiljneVarijable[maxIndexi[i]] < vrijednostCV:
                vrijednostCV = moguceVrijednostiCiljneVarijable[maxIndexi[i]]

        return vrijednostCV

    if(len(indexi)) == 1:
        return originalnaListaVrijednostiCiljneVarijable[indexi[0]]

    return neodredeneGrane(grana)

def izgenerirajCvorove(entropijaPodskupa, maxIG, ispisaneZnacajke, razina, grana):
    for i in range(0, len(entropijaPodskupa)):
        grana2 = []
        for j in range(len(grana)):
            grana2.append(grana[j])
        string = str(maxIG) + "," + moguceVrijednostiZnacajki[maxIG][i]
        grana2.append(string)
        if entropijaPodskupa[i] == 0 or len(ispisaneZnacajke) == len(znacajke) or maxDepth == razina:
            if len(grane) == 92:
                a = 0
            vrijednostCiljneVarijable = odrediVrijednostCiljneVarijable(grana2)
            string = ciljnaVarijabla + "," + vrijednostCiljneVarijable
            grana2.append(string)
            grane.append(grana2)
            continue
        else:
            updatePotrebneVrijednosti(grana2)
            izbrojMoguceVrijednostiZnacajki()
            izbrojMoguceVrijednostiCiljneVarijable()
            napraviTablicu()
            popuniTablicu()
            prebrojiZnacajke()
            izracunajEntropiju(entropijaPodskupa[i])
            izracunajIG(razina, entropijaPodskupa[i], grana2)
            maxIG2 = ispis(razina, ispisaneZnacajke)

            if maxIG2 != -1:
                entropijaPodskupa2 = entropija[maxIG2]
                ispisaneZnacajke2 = []
                for j in range (len(ispisaneZnacajke)):
                    ispisaneZnacajke2.append(ispisaneZnacajke[j])
                ispisaneZnacajke2.append(maxIG2)
                #if razina == 2:
                    #print(znacajke[maxIG])
                    #print(IG)
                izgenerirajCvorove(entropijaPodskupa2, maxIG2, ispisaneZnacajke2, razina + 1, grana2)
    return

def neodredeneGrane(grana):
    global originalnaListaVrijednosti
    global originalnaListaVrijednostiCiljneVarijable

    potrebnaListaVrijednostiPrije = originalnaListaVrijednosti
    potrebnaListaVrijednostiCiljneVarijablePrije = originalnaListaVrijednostiCiljneVarijable
    potrebnaListaVrijednostiPoslije = []
    potrebnaListaVrijednostiCiljneVarijablePoslije = []

    for i in range(len(grana)):
        cvor = grana[i]
        index = int(cvor.split(",")[0])
        vrijednostZnacajke = cvor.split(",")[1]
        potrebnaListaVrijednostiPoslije = []
        potrebnaListaVrijednostiCiljneVarijablePoslije = []
        for j in range(len(potrebnaListaVrijednostiPrije)):
            redVrijednosti = potrebnaListaVrijednostiPrije[j]
            if redVrijednosti[index] == vrijednostZnacajke:
                potrebnaListaVrijednostiPoslije.append(redVrijednosti)
                potrebnaListaVrijednostiCiljneVarijablePoslije.append(potrebnaListaVrijednostiCiljneVarijablePrije[j])
        if len(potrebnaListaVrijednostiPoslije) != 0:
            potrebnaListaVrijednostiPrije = potrebnaListaVrijednostiPoslije
            potrebnaListaVrijednostiCiljneVarijablePrije = potrebnaListaVrijednostiCiljneVarijablePoslije
        else:
            break

    brojCV = []
    for i in range(len(moguceVrijednostiCiljneVarijable)):
        vrijednostCV = moguceVrijednostiCiljneVarijable[i]
        br = 0
        for j in range(len(potrebnaListaVrijednostiPrije)):
            if potrebnaListaVrijednostiCiljneVarijablePrije[j] == vrijednostCV:
                br += 1
        brojCV.append(br)

    indexi = []
    broj = 0

    for i in range(len(brojCV)):
        if brojCV[i] > broj:
            broj = brojCV[i]


    for i in range(len(brojCV)):
        if brojCV[i] == broj:
            indexi.append(i)

    index = indexi[0]

    if len(indexi) > 1:
        for i in range(len(indexi)):
            if moguceVrijednostiCiljneVarijable[indexi[i]] < moguceVrijednostiCiljneVarijable[index]:
                index = indexi[i]


    return moguceVrijednostiCiljneVarijable[index]

def ucitajTest():
    file = open(sys.argv[2], "r")
    index = 0
    for line in file:
        line = line.rstrip()
        if index != 0:
            linijaVrijednosti = line.split(",")
            listaVrijednostiCiljneVarijableTest.append(linijaVrijednosti.pop())
            listaVrijednostiTest.append(linijaVrijednosti)
        index += 1

def predikcija():
    string = ""
    for i in range(len(listaVrijednostiTest)):
        if i == 41:
            a = 0
        linijaVrijednosti = listaVrijednostiTest[i]
        poklapanjeBool = False
        for j in range(len(grane)):
            grana = grane[j]
            poklapanje = 0
            for k in range(len(grana) - 1):
                indexZnacajke = int(grana[k].split(",")[0])
                vrijednostZnacajke = grana[k].split(",")[1]
                if linijaVrijednosti[indexZnacajke] == vrijednostZnacajke:
                    poklapanje += 1
            if poklapanje == len(grana) - 1:
                predikcijaCiljneVarijable.append(grana[-1].split(",")[1])
                string = string + grana[-1].split(",")[1] + " "
                poklapanjeBool = True
        if poklapanjeBool == False:
            nedovrsenaGrana = predikcijaNoveVrijednosti(linijaVrijednosti)
            pred = neodredeneGrane(nedovrsenaGrana)
            predikcijaCiljneVarijable.append(pred)
            string = string + pred + " "

    print(string)

def predikcijaNoveVrijednosti(linijaVrijednosti):
    staro = grane
    novo = []
    nedovrsenaGrana = []

    razina = 0
    while True:
        novo = []
        for i in range(len(staro)):
            grana = staro[i]
            indexZnacajke = int(grana[razina].split(",")[0])
            vrijednostZnacajke = grana[razina].split(",")[1]
            if linijaVrijednosti[indexZnacajke] == vrijednostZnacajke:
                novo.append(grana)
        razina += 1
        if len(novo) != 0:
            staro = novo
        else:
            nedovrsenaGrana = []
            for j in range(razina - 1):
                nedovrsenaGrana.append(staro[0][j])
            break

    return nedovrsenaGrana



def mjeraUspjesnosti():
    tocno = 0
    for i in range(len(listaVrijednostiCiljneVarijableTest)):
        if listaVrijednostiCiljneVarijableTest[i] == predikcijaCiljneVarijable[i]:
            tocno += 1
    tocnost = tocno / len(listaVrijednostiCiljneVarijableTest)
    print(tocnost)

def matricaZabune():
    matrica = []
    moguceVrijednostiCiljneVarijable.sort()
    for i in range(len(moguceVrijednostiCiljneVarijable)):
        red = []
        for j in range(len(moguceVrijednostiCiljneVarijable)):
            br = 0
            for k in range(len(listaVrijednostiCiljneVarijableTest)):
                if listaVrijednostiCiljneVarijableTest[k] == moguceVrijednostiCiljneVarijable[i] and predikcijaCiljneVarijable[k] == moguceVrijednostiCiljneVarijable[j]:
                    br += 1
            red.append(str(br))
        matrica.append(red)

    for i in range(len(matrica)):
        red = matrica[i]
        print(" ".join(red))


def ucitajConfig():
    global maxDepth

    file = open(sys.argv[3], "r")
    index = 0
    for line in file:
        line = line.rstrip()
        if index == 2:
            split = line.split("=")
            if len(split) > 1:
                maxDepth = int(split[1])
        index += 1

if __name__ == "__main__":
    ucitajConfig()
    ucitavanjeDatoteka()
    racunanjeEntropijuPocetnogSkupa()

    izbrojMoguceVrijednostiZnacajki()
    izbrojMoguceVrijednostiCiljneVarijable()
    napraviTablicu()
    popuniTablicu()
    prebrojiZnacajke()
    izracunajEntropiju(entropijaPocetnogSkupa)
    izracunajIG(0,entropijaPocetnogSkupa, [])

    razina = 0
    ispisaneZnacajke = []
    grana = []
    maxIG = ispis(razina, ispisaneZnacajke)
    ispisaneZnacajke.append(maxIG)
    entropijaPodskupa = entropija[maxIG]

    izgenerirajCvorove(entropijaPodskupa, maxIG, ispisaneZnacajke, razina + 1, grana)
    print(", ".join(stablo))

    ucitajTest()
    predikcija()
    mjeraUspjesnosti()
    matricaZabune()
